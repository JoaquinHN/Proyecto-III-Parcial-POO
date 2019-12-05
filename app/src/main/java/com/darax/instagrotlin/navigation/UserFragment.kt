package com.darax.instagrotlin.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.darax.instagrotlin.R
import com.darax.instagrotlin.navigation.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_user.view.*

class UserFragment: Fragment(){
    var fragmentView : View? = null
    var firestore : FirebaseFirestore? = null
    var uid : String? = null
    var auth : FirebaseAuth? = null
    var currentUserUid : String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView=LayoutInflater.from(activity).inflate(R.layout.fragment_user,container,false)
        uid=arguments?.getString("destinationUid")
        firestore= FirebaseFirestore.getInstance()
        auth= FirebaseAuth.getInstance()
        fragmentView?.account_reyclerview?.adapter = UserFragmentRecyclerViewAdapter()
        fragmentView?.account_reyclerview?.layoutManager= GridLayoutManager(activity!!,3)
        return fragmentView
    }

    inner class UserFragmentRecyclerViewAdapter :RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var contentDTO: ArrayList<ContentDTO> = arrayListOf()
        init {
            firestore?.collection("images")?.whereEqualTo("uid",uid)?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                //Aveces este query retorna null cuadno se cierra sesion
                if(querySnapshot == null ) return@addSnapshotListener
                //Conseguir los datos
                for(snapshot in querySnapshot.documents){
                    contentDTO.add(snapshot.toObject(ContentDTO::class.java)!!)
                }
                fragmentView?.account_tv_post_count?.text =contentDTO.size.toString()
                notifyDataSetChanged()
            }
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
           var with = resources.displayMetrics.widthPixels / 3
            var imageview= ImageView(parent.context)
            imageview.layoutParams = LinearLayoutCompat.LayoutParams(with,with)
            return CustomViewHolder(imageview)
        }
        inner class CustomViewHolder(var imageview: ImageView) : RecyclerView.ViewHolder(imageview)

        override fun getItemCount(): Int {
           return  contentDTO.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var imageView = (holder as CustomViewHolder).imageview
            Glide.with(holder.itemView.context).load(contentDTO[position].imageUrl).apply(RequestOptions().centerCrop()).into(imageView)
        }

    }
}