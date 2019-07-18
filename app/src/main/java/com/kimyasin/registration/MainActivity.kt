package com.kimyasin.registration

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.ticket.view.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var listNotes=ArrayList<Note>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Load from DB
        loadQuery("%")

    }

    override  fun onResume() {
        super.onResume()
        loadQuery("%")
    }

    fun loadQuery(title:String){

        val dbManager= DbManager(this)
        val projections= arrayOf("ID","Title","Description")
        val selectionArgs= arrayOf(title)
        val cursor=dbManager.Query(projections,"Title like ?",selectionArgs,"Title")
        listNotes.clear()
        if(cursor.moveToFirst()){

            do{
                val ID=cursor.getInt(cursor.getColumnIndex("ID"))
                val Title=cursor.getString(cursor.getColumnIndex("Title"))
                val Description=cursor.getString(cursor.getColumnIndex("Description"))

                listNotes.add(Note(ID, Title, Description))

            }while (cursor.moveToNext())
        }

        val myNotesAdapter= MyNotesAdpater(this, listNotes)
        lvNotes.adapter=myNotesAdapter


    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.main_menu, menu)

        val sv: SearchView = menu.findItem(R.id.app_bar_search).actionView as SearchView

        val sm= getSystemService(Context.SEARCH_SERVICE) as SearchManager
        sv.setSearchableInfo(sm.getSearchableInfo(componentName))
        sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                Toast.makeText(applicationContext, query, Toast.LENGTH_LONG).show()
                loadQuery("%"+ query +"%")
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })


        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if (item != null) {
            when(item.itemId){
                R.id.addNote ->{
                    //Got to add paage
                    val intent= Intent(this, AddNotes::class.java)
                    startActivity(intent)
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }


    inner class  MyNotesAdpater(context: Context, var listNotesAdpater: ArrayList<Note>) : BaseAdapter() {
        var context:Context?= context

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {

            var myView=layoutInflater.inflate(R.layout.ticket,null)
            var myNote=listNotesAdpater[p0]
            myView.tvTitle.text=myNote.NoteName
            myView.tvDescription.text=myNote.NoteDes
            myView.ivDelete.setOnClickListener( View.OnClickListener {
                var dbManager= DbManager(this.context!!)
                val selectionArgs= arrayOf(myNote.NoteID.toString())
                dbManager.Delete("ID=?",selectionArgs)
                loadQuery("%")
            })
            myView.ivEdit.setOnClickListener( View.OnClickListener{

                GoToUpdate(myNote)

            })
            return myView
        }

        override fun getItem(p0: Int): Any {
            return listNotesAdpater[p0]
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getCount(): Int {

            return listNotesAdpater.size

        }



    }


    fun GoToUpdate(note: Note){
        var intent=  Intent(this, AddNotes::class.java)
        intent.putExtra("ID",note.NoteID)
        intent.putExtra("name",note.NoteName)
        intent.putExtra("des",note.NoteDes)
        startActivity(intent)
    }


}
