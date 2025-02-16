package com.tolgadirek.chatwithme.adapter

import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tolgadirek.chatwithme.databinding.RecyclerRowBinding
import com.tolgadirek.chatwithme.model.Mesaj

class MesajAdapter(val mesajList : ArrayList<Mesaj>) : RecyclerView.Adapter<MesajAdapter.MesajHolder>() {
    class MesajHolder(val binding : RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MesajHolder {
        val recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MesajHolder(recyclerRowBinding)
    }

    override fun getItemCount(): Int {
        return mesajList.size
    }

    override fun onBindViewHolder(holder: MesajHolder, position: Int) {
        val mesaj = mesajList[position]
        if (mesaj.kullaniciMi) {

            holder.binding.kullaniciMesaj.text = mesaj.mesaj
            holder.binding.kullaniciMesaj.visibility = View.VISIBLE
            holder.binding.botMesaj.visibility = View.GONE
        } else {
            holder.binding.botMesaj.text = mesajFormat(mesaj.mesaj)
            holder.binding.botMesaj.visibility = View.VISIBLE
            holder.binding.kullaniciMesaj.visibility = View.GONE
        }
    }

    private fun mesajFormat(input: String): Spanned { //Spanned → HTML formatında bir çıktı döndürüyoruz çünkü Html.fromHtml() kullanacağız.
        // **Kalın metni HTML <b>...</b> etiketi ile değiştiriyoruz**
        val kalinFormat = input.replace(Regex("\\*\\*(.*?)\\*\\*")) { matchResult ->
            "<b>${matchResult.groupValues[1]}</b>"
            // Regex yakalama görevi görüyor.
            // \\*\\* → İki yıldız işaretini (**) algılar.
            // (.*?) → İçindeki herhangi bir metni yakalar.
            // \\*\\* → İki yıldız işaretiyle bittiğini kontrol eder.
        }

        // * olan yerleri yeni satır başında olacak şekilde • ile değiştiriyoruz
        val maddeFormat = kalinFormat.replace(Regex("\\*\\s(.*?)")) { matchResult ->
            "<br><br>• ${matchResult.groupValues[1]}" // Yeni satıra inen • işaretli madde
            // \\* → Yıldız (*) karakterini bul.
            // \\s → Boşluktan sonra gelen kelimeleri yakala.
            // (.*?) → Boşluktan sonra gelen tüm metni al.
        }

        return Html.fromHtml(maddeFormat, Html.FROM_HTML_MODE_LEGACY)
    }

}