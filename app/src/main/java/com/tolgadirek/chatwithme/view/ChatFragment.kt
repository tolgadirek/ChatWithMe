package com.tolgadirek.chatwithme.view

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.google.ai.client.generativeai.GenerativeModel
import com.tolgadirek.chatwithme.R
import com.tolgadirek.chatwithme.adapter.MesajAdapter
import com.tolgadirek.chatwithme.database.MesajDAO
import com.tolgadirek.chatwithme.database.MesajDatabase
import com.tolgadirek.chatwithme.databinding.FragmentChatBinding
import com.tolgadirek.chatwithme.model.Mesaj
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatFragment : Fragment(), PopupMenu.OnMenuItemClickListener {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: MesajDatabase
    private lateinit var mesajDao: MesajDAO

    private val mesajlist : ArrayList<Mesaj> = arrayListOf()
    private var adapter : MesajAdapter? = null

    private val api_key = "your-api-key"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = Room.databaseBuilder(requireContext(), MesajDatabase::class.java, "Mesajlar").build() // Telefonumuzun hafızasında duracak ismi belirledik
        mesajDao = db.mesajDao()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        sohbetYukle()

        adapter = MesajAdapter(mesajlist)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        binding.gonderButton.setOnClickListener { gonder(it) }
        binding.menuButton.setOnClickListener { menuButonTiklandi(it) }
    }

    private fun gonder(view: View) {
        val mesaj = binding.editText.text.toString().trim()
        if (mesaj.isNotEmpty()) {
            binding.textView.visibility = View.GONE
            val kullaniciMesaj = Mesaj(tarih = System.currentTimeMillis(), mesaj = mesaj, kullaniciMi = true)
            mesajlist.add(kullaniciMesaj)
            adapter?.notifyItemInserted(mesajlist.size - 1) // Son index ile adapteri uyar
            binding.recyclerView.scrollToPosition(mesajlist.size - 1) // Ekranı son mesaja kaydır

            lifecycleScope.launch(Dispatchers.IO) { // Veritabanına kaydetme işlemi arka planda çalışır.
                mesajDao.mesajEkle(kullaniciMesaj)
            }

            binding.editText.setText("") // Kullanıcı mesajı gönderince EditText temizlenir.
            cevapAl() // Botun cevabını al
        }
    }


    //asenkron işlem olduğu için suspend
    private suspend fun sohbetGecmisiGetir(): String {
        val mesajlar = mesajDao.tumMesajlariGetir() // DB'den tüm mesajları al
        return mesajlar.joinToString("\n") { mesaj ->
            if (mesaj.kullaniciMi)
                mesaj.mesaj
            else
                mesaj.mesaj
        }
    }

    private fun cevapAl(){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sohbetGecmisi = sohbetGecmisiGetir() // Veritabanından tüm mesajları al

                val generativeModel = GenerativeModel(modelName = "gemini-1.5-flash", apiKey = api_key)
                val cevap = generativeModel.generateContent(sohbetGecmisi) //Sohbet geçmişini apiye gönderip ona göre cevap almayı bekliyoruz.

                val botMesaj = cevap.text ?: "Üzgünüm, şu an cevap veremiyorum." //Botun cevabını al. Veremiyorsa stringi döndür.

                mesajDao.mesajEkle( Mesaj(tarih = System.currentTimeMillis(), mesaj = botMesaj, kullaniciMi = false)) // Bot mesajını daoya ekle

                requireActivity().runOnUiThread { //RecyclerView güncellemesi için arkada yapılan işten sonra arayüzün güncellenmesi gerekir. Bunu da Ana thread'te yapmak gerekir.
                    mesajlist.add( Mesaj(tarih = System.currentTimeMillis(), mesaj = botMesaj, kullaniciMi = false)) //Bot mesajını listeye ekle
                    adapter?.notifyItemInserted(mesajlist.size - 1) // Adaptörü listenin son indexteki mesajıyla uyar
                    binding.recyclerView.scrollToPosition(mesajlist.size - 1) // En son mesaja kaydır ekranı
                }
            } catch (e: Exception) {
                println(e)
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Bot yanıt veremedi!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun sohbetYukle() {
        lifecycleScope.launch(Dispatchers.IO) { // Yine veritabanı işlemi arka tarafta yapılır
            val mesajlar = mesajDao.tumMesajlariGetir()
            withContext(Dispatchers.Main) { // Kullanıcı arayüzünü etkilediği için bu yapıyı kullanırız
                mesajlist.clear()
                mesajlist.addAll(mesajlar)
                adapter?.notifyDataSetChanged()

                if (mesajlist.isNotEmpty()) {
                    binding.textView.visibility = View.GONE
                    binding.recyclerView.scrollToPosition(mesajlist.size - 1) // Son indexe otomatik ekranı kaydır.
                }
            }
        }
    }

    private fun menuButonTiklandi(view:View) {
        val popup = PopupMenu(requireContext(), binding.menuButton)
        val inflater  = popup.menuInflater
        inflater.inflate(R.menu.popup_menu,popup.menu)
        popup.setOnMenuItemClickListener(this)
        popup.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        if (item.itemId == R.id.temizlemenu) {
            val alert = AlertDialog.Builder(requireContext())
            alert.setTitle("Sohbeti Temizle")
            alert.setMessage("Sohbeti temizlemek istediğinize emin misiniz?")
            alert.setPositiveButton("Evet") { _, _ ->
                lifecycleScope.launch(Dispatchers.IO) {
                    mesajDao.tumMesajlariSil()
                    withContext(Dispatchers.Main) {
                        mesajlist.clear()
                        adapter?.notifyDataSetChanged()
                        binding.textView.visibility = View.VISIBLE
                    }
                }
            }
            alert.setNegativeButton("Hayır", null)
            alert.show()
        }
        return true
    }
}