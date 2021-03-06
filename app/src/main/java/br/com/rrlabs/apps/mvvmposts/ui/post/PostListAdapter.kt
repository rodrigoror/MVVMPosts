package br.com.rrlabs.apps.mvvmposts.ui.post

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import br.com.rrlabs.apps.mvvmposts.R
import br.com.rrlabs.apps.mvvmposts.databinding.ItemPostBinding
import br.com.rrlabs.apps.mvvmposts.model.Post

class PostListAdapter: RecyclerView.Adapter<PostListAdapter.ViewHolder>() {

    private lateinit var postList:List<Post>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostListAdapter.ViewHolder {
        var binding:ItemPostBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_post,parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return if(::postList.isInitialized)postList.size else 0
    }

    override fun onBindViewHolder(p0: PostListAdapter.ViewHolder, p1: Int) {
        p0.bind(postList[p1])
    }
    fun updatePostList(postList:List<Post>){
        this.postList = postList
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: ItemPostBinding):RecyclerView.ViewHolder(binding.root){
        private val viewModel = PostViewModel()

        fun bind(post:Post){
            viewModel.bind(post)
            binding.viewModel = viewModel
        }
    }

}