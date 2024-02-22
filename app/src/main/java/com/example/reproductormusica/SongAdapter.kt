package com.example.reproductormusica
// SongAdapter.kt
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SongAdapter(private val context: Context, private val songs: List<Song>, private val onSongClickListener: (Song) -> Unit) :
    RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    private var mediaPlayer: MediaPlayer? = null
    private var isUserSeeking = false
    private var lastPlaybackPosition: Int = 0

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("SongAdapter", Context.MODE_PRIVATE)
    }

    inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songNameTextView: TextView = itemView.findViewById(R.id.song_title)
        val playButton: Button = itemView.findViewById(R.id.button_play)
        val pauseButton: Button = itemView.findViewById(R.id.button_pause)
        val replayButton: Button = itemView.findViewById(R.id.button_replay)
        val seekBar: SeekBar = itemView.findViewById(R.id.seek_bar)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val song = songs[position]
                    onSongClickListener(song)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item, parent, false)
        return SongViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val currentSong = songs[position]
        holder.songNameTextView.text = currentSong.name

        holder.playButton.setOnClickListener {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(holder.itemView.context, currentSong.resourceId)
            mediaPlayer?.start()
            mediaPlayer?.seekTo(lastPlaybackPosition)
            startSeekBar(holder)
        }

        holder.pauseButton.setOnClickListener {
            mediaPlayer?.pause()
            lastPlaybackPosition = mediaPlayer?.currentPosition ?: 0
            savePlaybackPosition(lastPlaybackPosition)
        }

        holder.replayButton.setOnClickListener {
            mediaPlayer?.seekTo(0)
            mediaPlayer?.start()
            startSeekBar(holder)
        }

        holder.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isUserSeeking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                isUserSeeking = false
            }
        })
    }

    override fun getItemCount() = songs.size

    private fun startSeekBar(holder: SongViewHolder) {
        holder.seekBar.max = mediaPlayer?.duration ?: 0
        val handler = android.os.Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (!isUserSeeking) {
                    holder.seekBar.progress = mediaPlayer?.currentPosition ?: 0
                }
                handler.postDelayed(this, 1000)
            }
        }, 0)
    }

    private fun savePlaybackPosition(position: Int) {
        sharedPreferences.edit().putInt("lastPlaybackPosition", position).apply()
    }

    private fun getLastPlaybackPosition(): Int {
        return sharedPreferences.getInt("lastPlaybackPosition", 0)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
