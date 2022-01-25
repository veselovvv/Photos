package com.veselovvv.photos

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PhotosFragment : Fragment() {

    private lateinit var photosViewModel: PhotosViewModel
    private lateinit var thumbnailDownloader: ThumbnailDownloader<PhotoHolder>
    private lateinit var photoRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Сохранение PhotosFragment:
        retainInstance = true

        // Ссылка на ViewModel:
        photosViewModel = ViewModelProviders.of(this).get(PhotosViewModel::class.java)

        val responseHandler = Handler()

        thumbnailDownloader = ThumbnailDownloader(responseHandler) { photoHolder, bitmap ->
            val drawable = BitmapDrawable(resources, bitmap)
            photoHolder.bindDrawable(drawable)
        }

        // Подписка thumbnailDownloader на получение обратных вызовов жизненного цикла из PhotosFragment:
        lifecycle.addObserver(thumbnailDownloader.fragmentLifecycleObserver)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Регистрация наблюдателя жизненного цикла представления:
        viewLifecycleOwner.lifecycle.addObserver(thumbnailDownloader.viewLifecycleObserver)

        val view = inflater.inflate(R.layout.fragment_photos, container, false)

        photoRecyclerView = view.findViewById(R.id.photo_recycler_view)
        photoRecyclerView.layoutManager = GridLayoutManager(context, 2)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        photosViewModel.photoLiveData.observe(viewLifecycleOwner, Observer { photos ->
            photoRecyclerView.adapter = PhotoAdapter(photos)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Отказ от регистрации наблюдателя жизненного цикла представления:
        viewLifecycleOwner.lifecycle.removeObserver(thumbnailDownloader.viewLifecycleObserver)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Отписка thumbnailDownloader от получения обратных вызовов жизненного цикла из PhotosFragment:
        lifecycle.removeObserver(thumbnailDownloader.fragmentLifecycleObserver)
    }

    private inner class PhotoHolder(private val imageView: ImageView)
        : RecyclerView.ViewHolder(imageView) {
        val bindDrawable: (Drawable) -> Unit = imageView::setImageDrawable
    }

    private inner class PhotoAdapter(private val photos: List<Photo>) : RecyclerView.Adapter<PhotoHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
            val view = layoutInflater.inflate(R.layout.list_photo, parent, false) as ImageView
            return PhotoHolder(view)
        }

        override fun getItemCount(): Int = photos.size

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            val photo = photos[position]
            val placeHolder: Drawable = ContextCompat.getDrawable(requireContext(), R.drawable.image)
                ?: ColorDrawable()

            holder.bindDrawable(placeHolder)

            // Передача целевой папки PhotoHolder, где будет размещено изображение и URL-адреса Photo для скачивания:
            thumbnailDownloader.queueThumbnail(holder, photo.url)
        }
    }

    companion object {
        fun newInstance() = PhotosFragment()
    }
}