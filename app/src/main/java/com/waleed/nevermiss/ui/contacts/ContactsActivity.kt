package com.waleed.nevermiss.ui.contacts

import android.app.Activity
import android.content.ContentResolver
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.ContactsContract
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.waleed.nevermiss.R
import com.waleed.nevermiss.Repo.Room.DataBaseRepo
import com.waleed.nevermiss.model.Contact
import com.waleed.nevermiss.model.Groups
import com.waleed.nevermiss.utils.Utils
import kotlinx.android.synthetic.main.activity_contacts.*


class ContactsActivity : AppCompatActivity() {

    lateinit var contactSet: ArrayList<Contact>
    lateinit var setTemp: HashSet<Contact>
    lateinit var contactsAdapter: ContactsAdapter
    var withGroups: Boolean = false
    lateinit var groupsAdapter: GroupsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)
        setSupportActionBar(toolbar)

        contactSet = ArrayList()
        setTemp = HashSet<Contact>()
        contactsAdapter = ContactsAdapter()

        val layoutManager = LinearLayoutManager(this)
        contactRecyclerView.layoutManager = layoutManager
        contactRecyclerView.adapter = contactsAdapter


        withGroups = intent.extras!!.getBoolean("withGroups", false)

        // groups part
        if (withGroups) {
            // withGroups = intent.extras!!.getBoolean("withGroups")
            groupsAdapter = GroupsAdapter()
            val gLayoutManager = LinearLayoutManager(this)
            groupRecyclerView.layoutManager = gLayoutManager
            groupRecyclerView.adapter = groupsAdapter
            groupRecyclerView.visibility = View.VISIBLE
            groupsTextView.visibility = View.VISIBLE

            getGroups()
        } else {
            // withGroups = false
            groupRecyclerView.visibility = View.GONE
            groupsTextView.visibility = View.GONE
        }



        getContactList()

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_save, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_done -> {

                var contacts = ArrayList<Contact>()
                if (withGroups) {

                    if (groupsAdapter.getSelectedContacts().isNotEmpty()) {
                        contacts.addAll(groupsAdapter.getSelectedContacts())
                    }

                    if (contactsAdapter.getSelectedContacts().isNotEmpty()) {
                        contacts.addAll(contactsAdapter.getSelectedContacts())
                    }

                    if (contacts.isNotEmpty()) {
                        intent.putParcelableArrayListExtra("contactList", contacts)
                        setResult(Activity.RESULT_OK, intent)
                    } else {
                        setResult(Activity.RESULT_CANCELED, intent)

                    }
                    finish()
                    Log.d(
                        "selectedData",
                        "selected data = " + contactsAdapter.getSelectedContacts()
                    )


                } else {
                    if (contactsAdapter.getSelectedContacts().isNotEmpty()) {

                        contacts = contactsAdapter.getSelectedContacts() as ArrayList<Contact>

                        intent.putParcelableArrayListExtra("contactList", contacts)

                        setResult(Activity.RESULT_OK, intent)
                    } else {
                        setResult(Activity.RESULT_CANCELED, intent)

                    }
                    finish()
                    // contactsAdapter.getSelectedContacts()
                    Log.d(
                        "selectedData",
                        "selected data = " + contactsAdapter.getSelectedContacts()
                    )
                }


                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


//    private fun getContactList() {
//
//        val cr = contentResolver
//
//        val cur = cr.query(
//            ContactsContract.Contacts.CONTENT_URI, null,
//            null, null, null
//        )
//
//        if (cur?.count ?: 0 > 0) {
//            while (cur != null && cur.moveToNext()) {
//                val id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID))
//                val name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
//
//                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
//
//                    val pCur = cr.query(
//                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                        null,
//                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
//                        arrayOf(id),
//                        null
//                    )
//
//                    while (pCur!!.moveToNext()) {
//                        val phoneNo =
//                            pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
////                        Log.d("getContactList", "Name: $name")
////                        Log.d("getContactList", "Phone Number: $phoneNo")
//
//
//                        setTemp.add(Contact(name, phoneNo, false))
//                        //  contactSet.add(Contact(name, phoneNo, false))
//                    }
//                    pCur.close()
//
//                    val contactSet = ArrayList<Contact>()
//                    for (subset in setTemp) {
//                        contactSet.add(subset)
//                    }
//                    contactsAdapter.setContactList(contactSet)
//                }
//            }
//        }
//        cur?.close()
//    }


    private fun getGroups() {

        var dbRepo = DataBaseRepo(this)

        class GetUserGroups : AsyncTask<Void, Void, List<Groups>>() {

            override fun doInBackground(vararg avoid: Void): List<Groups> {
                return dbRepo.db.getUserGroups(Utils.getCurrentUser())
            }

            override fun onPostExecute(groups: List<Groups>) {
                super.onPostExecute(groups)
                groupsAdapter.setGroupsList(groups)

            }
        }

        GetUserGroups().execute()
    }


    private fun getContactList() {
        val whatsList = getWhatsList()
        val contactList = ArrayList<Contact>()
        val cr: ContentResolver = contentResolver
        val cur = cr.query(
            ContactsContract.Contacts.CONTENT_URI, null,
            null, null, null
        )

        if (cur?.count ?: 0 > 0) {
            while (cur != null && cur.moveToNext()) {
                val id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID))
                val name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))

                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {

                    val pCur = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id),
                        null
                    )
                    while (pCur!!.moveToNext()) {
                        var phoneNo =
                            pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                        phoneNo = phoneNo.replace(" ", "")

                        var hasWhatApp = false
                        for (test in whatsList) {
                            if (phoneNo == test.number) {
                                hasWhatApp = true
                                break
                            }
                        }
                        contactList.add(Contact(name, phoneNo, hasWhatApp, false))

                    }
                    pCur.close()
                }
            }
        }
        cur?.close()
        contactsAdapter.setContactList(contactList)

    }

    private fun getWhatsList(): ArrayList<Contact> {
        //This class provides applications access to the content model.
        val cr = contentResolver

        //RowContacts for filter Account Types
        val contactCursor = cr.query(
            ContactsContract.RawContacts.CONTENT_URI,
            arrayOf(ContactsContract.RawContacts._ID, ContactsContract.RawContacts.CONTACT_ID),
            ContactsContract.RawContacts.ACCOUNT_TYPE + "= ?",
            arrayOf("com.whatsapp"), null
        )

        //ArrayList for Store Whatsapp Contact
        val myWhatsappContacts = ArrayList<Contact>()

        if (contactCursor != null) {
            if (contactCursor!!.getCount() > 0) {
                if (contactCursor!!.moveToFirst()) {
                    do {
                        //whatsappContactId for get Number,Name,Id ect... from  ContactsContract.CommonDataKinds.Phone
                        val whatsappContactId = contactCursor!!.getString(
                            contactCursor!!.getColumnIndex(ContactsContract.RawContacts.CONTACT_ID)
                        )

                        if (whatsappContactId != null) {
                            //Get Data from ContactsContract.CommonDataKinds.Phone of Specific CONTACT_ID
                            val whatsAppContactCursor = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                arrayOf(
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                                ),
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                arrayOf<String>(whatsappContactId), null
                            )

                            if (whatsAppContactCursor != null) {
                                whatsAppContactCursor!!.moveToFirst()
                                val id = whatsAppContactCursor!!.getString(
                                    whatsAppContactCursor!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                                )
                                var name = whatsAppContactCursor!!.getString(
                                    whatsAppContactCursor!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                                )
                                var phoneNo = whatsAppContactCursor!!.getString(
                                    whatsAppContactCursor!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                )
                                phoneNo = phoneNo.replace(" ", "")
                                var hasWhatsApp = true
                                whatsAppContactCursor!!.close()

                                //Add Number to ArrayList
                                myWhatsappContacts.add(Contact(name, phoneNo, hasWhatsApp, false))

                            }
                        }

                    } while (contactCursor!!.moveToNext())
                    contactCursor!!.close()
                }
            }
        }
        return myWhatsappContacts
    }
}


