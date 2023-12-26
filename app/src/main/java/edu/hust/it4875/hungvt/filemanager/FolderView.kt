package edu.hust.it4875.hungvt.filemanager

import android.app.AlertDialog
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import edu.hust.it4875.hungvt.filemanager.databinding.FolderEntryBinding
import java.io.File

class FolderView(
    private var context: AppCompatActivity,
    private var current: File = Environment.getExternalStorageDirectory()
) : RecyclerView.Adapter<FolderView.ViewHolder>() {
    private val entries: ArrayList<String> = ArrayList<String>()
    private lateinit var binding: FolderEntryBinding
    inner class ViewHolder(
        val binding: FolderEntryBinding): RecyclerView.ViewHolder(binding.root) {
    }
    init {
        reloadEntries()
    }
    private fun reloadEntries() {
        entries.clear()
        val list = current.list { file, name -> !name.startsWith(".") }
        if (list != null) {
            entries.addAll(list) // No dot files
            entries.sort()
        }

        notifyDataSetChanged()
        Log.v("folderViewReload", "loaded ${entries.size} entries")
    }
    fun goUp() {
        val next = current.parentFile
        if (next == null || !next.canRead()) return
        Log.v("folderViewChangeDir", "chdir $current -> $next")
        current = next
        reloadEntries()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(FolderEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
    override fun getItemCount(): Int {
        return entries.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.folderEntryText.text = entries[position]
        val entry = File(current, entries[position])
        if (entry.isFile) {
            holder.binding.folderEntryIcon.setImageResource(R.mipmap.icon_file)
        } else {
            holder.binding.folderEntryIcon.setImageResource(R.mipmap.icon_folder)
        }
        holder.binding.folderEntryText.setOnClickListener { access(entry) }
        holder.binding.folderEntryIcon.setOnClickListener { access(entry) }
    }

    fun access(entry: File) {
        Log.v("folderViewEntry", "clicked $entry" )
        if (entry.isDirectory) {
            Log.v("folderViewChangeDir", "chdir $current -> $entry")
            current = entry
            reloadEntries()
        } else {
            Log.v("folderViewOpenFile", "open $entry")
            AlertDialog.Builder(context)
                .setTitle("Function not yet implemented")
                .setMessage("Opening file ${entry.name}")
                .show()
        }
    }
}