package com.waleed.nevermiss.Repo.Room

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.waleed.nevermiss.model.Groups
import com.waleed.nevermiss.model.MyMessage
import com.waleed.nevermiss.ui.fragment.group.GroupViewModel
import com.waleed.nevermiss.ui.fragment.history.HistoryViewModel
import com.waleed.nevermiss.ui.fragment.message.MessageViewModel
import com.waleed.nevermiss.ui.sendMessage.SendMessageViewModel
import com.waleed.nevermiss.utils.Utils

class DataBaseRepo {

    private var context: Context? = null
    lateinit var groupViewModel: GroupViewModel
    lateinit var sendMessageViewModel: SendMessageViewModel
    lateinit var messageViewModel: MessageViewModel
    lateinit var historyViewModel: HistoryViewModel

    val db: RoomDAO get() = NeverMissDataBase.getDatabase(context!!).RoomDao()

    //-------------- groups -------------------
    constructor(context: Context, groupViewModel: GroupViewModel) {
        this.context = context
        this.groupViewModel = groupViewModel
    }

    fun insertGroup(group: Groups) {

        class InsertGroup : AsyncTask<Groups, Void, Long>() {

            override fun doInBackground(vararg groups: Groups): Long? {
                return db.insertGroup(group)
            }

            override fun onPostExecute(aLong: Long?) {
                super.onPostExecute(aLong)

                if (aLong!! > -1L) {
                    groupViewModel.showMessage("add Successfully")
                } else {
                    groupViewModel.showError("Error, Try Again")

                }
            }
        }

        InsertGroup().execute(group)

    }

    fun updateGroup(group: Groups) {

        class UpdateGroup : AsyncTask<Groups, Void, Int>() {

            override fun doInBackground(vararg groups: Groups): Int? {
                return db.updateGroup(group)

            }

            override fun onPostExecute(aInt: Int) {
                super.onPostExecute(aInt)

                if (aInt != null) {
                    groupViewModel.showMessage("Update Successfully")
                } else {
                    groupViewModel.showError("Error, Can't Update")
                }
            }
        }

        UpdateGroup().execute(group)

    }

    fun deleteGroup(group: Groups) {

        class DeleteGroup : AsyncTask<Groups, Void, Void>() {
            override fun doInBackground(vararg groups: Groups): Void? {
                db.deleteGroup(group)
                return null
            }

//            override fun onPostExecute(aVoid: Void) {
//                super.onPostExecute(aVoid)
//            }
        }

        DeleteGroup().execute(group)

    }

    fun getUserGroups(mutableLiveData: MutableLiveData<List<Groups>>, currentUser: String) {

        class GetUserGroups : AsyncTask<Void, Void, List<Groups>>() {

            override fun doInBackground(vararg avoid: Void): List<Groups> {
                return db.getUserGroups(currentUser)
            }

            override fun onPostExecute(groups: List<Groups>) {
                super.onPostExecute(groups)
                mutableLiveData.value = groups

            }
        }

        GetUserGroups().execute()
    }

    fun getGroup(groupId: Long, currentUser: String) {

        class GetGroup : AsyncTask<Void, Void, Groups>() {
            override fun doInBackground(vararg p0: Void?): Groups {
                return db.getGroup(groupId, currentUser)
            }

            override fun onPostExecute(result: Groups) {
                super.onPostExecute(result)
            }
        }

        GetGroup().execute()

    }



    //---------------- myMessage--------------------
    constructor(context: Context, sendMessageViewModel: SendMessageViewModel) {
        this.context = context
        this.sendMessageViewModel = sendMessageViewModel
    }


    constructor(context: Context, messageViewModel: MessageViewModel) {
        this.context = context
        this.messageViewModel = messageViewModel
    }

    constructor(context: Context, historyViewModel: HistoryViewModel) {
        this.context = context
        this.historyViewModel = historyViewModel
    }

    constructor(context: Context) {
        this.context = context
    }


    fun getMessage(messageId: String) {

        class GetMessage : AsyncTask<String, Void, MyMessage>() {

            protected override fun doInBackground(vararg longs: String): MyMessage {
                return db.getMessage(messageId, Utils.getCurrentUser())
            }

            override fun onPostExecute(myMessage: MyMessage) {
                super.onPostExecute(myMessage)
            }
        }

        GetMessage().execute(messageId)

    }

    fun setMessage(myMessage: MyMessage) {

        class SetMessage : AsyncTask<MyMessage, Void, Long>() {

            override fun doInBackground(vararg myMessages: MyMessage): Long? {
                return db.insertMessage(myMessage)
            }
            override fun onPostExecute(aLong: Long?) {
                super.onPostExecute(aLong)
                myMessage.smsId = aLong!!
                sendMessageViewModel.setAlarm(myMessage)
                Log.d("tripIdForAlarm", " repo onPostExecute: trip id =" + aLong!!)
                sendMessageViewModel.showResult("add Successfully")

            }
        }

        SetMessage().execute(myMessage)

    }

    fun updateMessage(myMessage: MyMessage) {

        class UpdateMessages : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg voids: Void): Void? {
                db.updateMessage(myMessage)
                return null
            }
//            override fun onPostExecute(aVoid: Void) {
//                super.onPostExecute(aVoid)
////                sendMessageViewModel.updateAlarm(myMessage)
////                sendMessageViewModel.showResult("update Successfully")
//            }
        }

        UpdateMessages().execute()

    }

    fun updateMessageState(id: String, state: String) {

        class UpdateMessageState : AsyncTask<Void, Void, Void>() {

            override fun doInBackground(vararg voids: Void): Void? {
                db.updateMessageState(id, state)
                return null
            }

//            override fun onPostExecute(aVoid: Void) {
//                super.onPostExecute(aVoid)
//
//            }
        }

        UpdateMessageState().execute()

    }


    fun deleteMessage(myMessage: MyMessage) {

        class DeleteMessage : AsyncTask<MyMessage, Void, Void>() {

            override fun doInBackground(vararg myMessages: MyMessage): Void? {
                db.deleteMessage(myMessage)
                return null
            }

//            override fun onPostExecute(aVoid: Void) {
//                super.onPostExecute(aVoid)
//            }
        }

        DeleteMessage().execute(myMessage)

    }

    fun getPendingMessages(mutableLiveData: MutableLiveData<List<MyMessage>>) {

        class GetMessage : AsyncTask<Void, Void, List<MyMessage>>() {

            override fun doInBackground(vararg avoid: Void): List<MyMessage> {
                return db.getUserMessages(Utils.getCurrentUser(), "pending")
                //return db.getUserAllMessages(Utils.getCurrentUser())
            }

            override fun onPostExecute(myMessages: List<MyMessage>) {
                super.onPostExecute(myMessages)
                mutableLiveData.value = myMessages

            }
        }

        GetMessage().execute()
    }

    fun getAllMessages(mutableLiveData: MutableLiveData<List<MyMessage>>) {

        class GetAllMessages : AsyncTask<Void, Void, List<MyMessage>>() {

            override fun doInBackground(vararg avoid: Void): List<MyMessage> {
                return db.getUserAllMessages(Utils.getCurrentUser())
            }

            override fun onPostExecute(myMessages: List<MyMessage>) {
                super.onPostExecute(myMessages)
                mutableLiveData.value = myMessages

            }
        }

        GetAllMessages().execute()
    }


}
