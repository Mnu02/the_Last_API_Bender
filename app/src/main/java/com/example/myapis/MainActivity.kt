package com.example.myapis

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    var avatarURL = ""
    var characterName = ""
    var id = ""

    var avatarList: MutableList<JSONObject> = mutableListOf()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getHeroList()
        var button = findViewById<Button>(R.id.button)
        var imageView = findViewById<ImageView>(R.id.nasaPic)
        var text1 = findViewById<TextView>(R.id.firstTextView)
        var text2 = findViewById<TextView>(R.id.secondTextView)
        var text3 = findViewById<TextView>(R.id.thirdTextView)
        getNextImage(button, imageView, text1, text2, text3)
    }

    private fun getNextImage(button: Button, imageView: ImageView, t1: TextView, t2: TextView, t3: TextView) {
        button.setOnClickListener {
            getHeroImageURL()
            t3.text = characterName

            try {
                Glide.with(this)
                    .load(avatarURL)
                    .fitCenter()
                    .into(imageView)
            } catch (e: Exception) {
                Log.d("Hello", "$e")
                getNextImage(button, imageView, t1, t2, t3)
            }
        }
    }

    // Check if the name is valid (ex. not some code like DH32A or characters from a foreign language)
    fun isValidName(name: String): Boolean {
        // This pattern checks if it only includes english characters and space (accepts A-Z, a-z, and space)
        val pattern = Regex("[A-Za-z ]+")

        // If the pattern is not in english, it isn't valid (return false)
        if (!pattern.matches(name)) {
            return false
        }

        // If the pattern doesn't include lowercase (ex. it filters out DH32A) then we return false
        var hasLowerCase = false;
        for (c in name) {
            if (c.isLowerCase()) {
                hasLowerCase = true;
            }

        }

        return hasLowerCase
    }

    // Here I made an API call once that populates a list. This list is filtered here so that we don't have any weird JSON objects
    private fun getHeroList() {
        val client = AsyncHttpClient()

        client["https://api.sampleapis.com/avatar/characters", object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JsonHttpResponseHandler.JSON) {
                //Log.d("NationalFlags", "response successful$json")

                for (i in 0 until json.jsonArray.length()) {
                    val jsonObject = json.jsonArray.getJSONObject(i)
                    if (!jsonObject.isNull("name")) {
                        if (isValidName(jsonObject.getString("name"))) {
                            avatarList.add(jsonObject)
                        }
                    }
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                errorResponse: String,
                throwable: Throwable?
            ) {
                Log.d("NationalFlags Error", errorResponse)
            }
        }]
    }

    // Since we already have a list, we don't need to make an API call when we click the next button, instead we just grab a random character from the list
    private fun getHeroImageURL() {
        val randomNumber = Random.nextInt(avatarList.size)

        var avatarCharacter = avatarList[randomNumber];
        avatarURL = avatarCharacter.getString("image")
        Log.d("AvatarURL", "URL set to -->$avatarURL")

        characterName = avatarCharacter.getString("name")
        Log.d("AvatarName", "Name set to -->$characterName")

        id = avatarCharacter.getString("id")
    }
}