package com.example.reference.ui.slideshow

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.reference.databinding.FragmentSlideshowBinding
import java.util.Date

class SlideshowFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(SlideshowViewModel::class.java)

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textSlideshow
        slideshowViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val filename = "myfile"
        val fileContents = "Hello world!"
        requireContext().openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(fileContents.toByteArray())
        }
        var files: Array<String> = requireContext().fileList()

        val db = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "database-name"
        ).build()

        val userDao = db.userDao()
        val users: List<User> = userDao.getAll()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @Database(entities = [User::class], version = 1)
    abstract class AppDatabase : RoomDatabase() {
        abstract fun userDao(): UserDao
    }
    @Entity
    data class User(
        @PrimaryKey(autoGenerate = true) val uid: Int = 0,
        @ColumnInfo(name = "picture_text") val pictureText: String?,
        @ColumnInfo(name = "source_picture") val picture: Bitmap?,
        @ColumnInfo(name = "date") val date: Date
    )

    @Dao
    interface UserDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insert(user: User): Long

        @Query("SELECT * FROM user")
        fun getAll(): List<User>

        @Query("SELECT * FROM user WHERE uid IN (:userIds)")
        fun loadAllByIds(userIds: IntArray): List<User>

        @Query("SELECT * FROM user WHERE date BETWEEN :startDate AND :endDate")
        fun getUsersWithinDateRange(startDate: Date, endDate: Date): List<User>

        @Insert
        fun insertAll(vararg users: User)

        @Delete
        fun delete(user: User)
    }



}