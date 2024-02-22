package com.example.reproductormusica
// MainActivity.kt
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private val songs = listOf(
        Song("Ghost", R.raw.ghost),
        Song("Cielo Hermético", R.raw.cielohermetico),
        Song("El meu gos se'n va", R.raw.elmeugossenva),
        Song("Not meant to be", R.raw.notmeanttobe)
        // Agrega más canciones aquí según sea necesario
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = SongAdapter(this,songs) { song ->
            val intent = Intent(this, SongAdapter::class.java).apply {
                putExtra("song_title", song.name)
                putExtra("song_resource_id", song.resourceId)
            }
            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }
}