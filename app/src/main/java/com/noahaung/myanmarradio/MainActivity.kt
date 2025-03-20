package com.noahaung.myanmarradio

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.media3.common.Player
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private lateinit var miniPlayerContainer: View
    private lateinit var miniPlayerStationName: TextView
    private lateinit var miniPlayerPlayPause: ImageButton
    private lateinit var miniPlayerImage: ImageView
    private lateinit var stations: ArrayList<Station>
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var viewPager: ViewPager2

    private val playerListener = object : Player.Listener {
        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            updateMiniPlayer()
        }

        override fun onPlaybackStateChanged(state: Int) {
            updateMiniPlayer()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PlaybackManager.initialize(this)

        sharedPreferences = getSharedPreferences("favorites", MODE_PRIVATE)

        stations = arrayListOf(
            Station("Friends FM", "https://stream-172.zeno.fm/rtt1xsez338uv", R.drawable.friends_fm),
            Station("City FM", "https://stream.zeno.fm/rq4ux25pyeruv", R.drawable.city_fm),
            Station("Shwe Ayeyar FM", "https://stream.zeno.fm/gvyz1utf4uhvv", R.drawable.shwe_ayeyar_fm),
            Station("Shwe Hinthar", "https://stream-172.zeno.fm/pxn7p5cbm48uv", R.drawable.shwe_hinthar),
            Station("Mingalar Par Radio", "https://stream-175.zeno.fm/hnsabdeu7k0uv", R.drawable.mingalar_par_radio),
            Station("Dhamma", "https://stream-176.zeno.fm/v3awsfmhnchvv", R.drawable.dhamma),
            Station("MyaYoungChi Radio", "https://stream-174.zeno.fm/ggjbzeqpkawvv", R.drawable.myayoungchi_radio),
            Station("Cherry FM", "https://stream-169.zeno.fm/4x8x3g06n48uv", R.drawable.cherry_fm),
            Station("Melody FM", "https://stream-175.zeno.fm/yajlk8od0tmuv", R.drawable.melody_fm),
            Station("Nevermore Channel", "https://stream-176.zeno.fm/wh799nuwvchvv", R.drawable.nevermore_channel),
            Station("ARAKAN FM", "https://stream-175.zeno.fm/p9k212xkp2zuv", R.drawable.arakan_fm),
            Station("The Room Yangon", "https://stream-172.zeno.fm/tfft925h5a0uv", R.drawable.the_room_yangon),
            Station("MCOMMM", "https://stream-175.zeno.fm/u4w6fu3nhm8uv", R.drawable.mcommm),
            Station("Sri Ksetra", "https://stream-172.zeno.fm/pdrht65hgdtuv", R.drawable.sri_ksetra),
            Station("Relax Zone", "https://stream-173.zeno.fm/ji4wajqtih7tv", R.drawable.relax_zone),
            Station("Burma Revolution Radio", "https://stream-173.zeno.fm/00m4g8wr1p8uv", R.drawable.burma_revolution_radio),
            Station("Advent FM", "https://stream-176.zeno.fm/tus18w05na0uv", R.drawable.advent_fm),
            Station("Arakan FM", "https://stream-173.zeno.fm/uhuyx8fve48uv", R.drawable.arakan_fm),
            Station("Myanmar", "https://stream-173.zeno.fm/q9cw34n6a48uv", R.drawable.myanmar),
            Station("Heaven Life", "https://stream-169.zeno.fm/ka3qyytcabjuv", R.drawable.heaven_life),
            Station("Karen Gospel Radio", "https://stream-171.zeno.fm/17ke6qyhtp8uv", R.drawable.karen_gospel_radio),
            Station("Voice Sports News", "https://stream-174.zeno.fm/zbh110758a0uv", R.drawable.voice_sports_news),
            Station("Shwe90", "https://stream-164.zeno.fm/awh41anxg7atv", R.drawable.shwe90),
            Station("Myanmar Truckers FM", "https://stream-176.zeno.fm/gh0t0wugy38uv", R.drawable.myanmar_truckers_fm),
            Station("Springtime", "https://stream-163.zeno.fm/9500n7ywaq8uv", R.drawable.springtime),
            Station("ABC Radio", "https://stream-176.zeno.fm/o2mneumproruv", R.drawable.abc_radio),
            Station("Jazz Live Radio", "https://stream-176.zeno.fm/2txc199hsp8uv", R.drawable.jazz_live_radio),
            Station("Dr.Snail Band", "https://stream-163.zeno.fm/irtxizbdrnctv", R.drawable.dr_snail_band),
            Station("MCOMM Country Live Radio", "https://stream-171.zeno.fm/k3cms4d6wp8uv", R.drawable.mcomm_country_live_radio),
            Station("Burma Tamil Community", "https://stream-173.zeno.fm/cbk2avkrzc9uv", R.drawable.burma_revolution_radio),
            Station("Star-Thura", "https://stream-164.zeno.fm/qalothzu8hutv", R.drawable.star_thura),
            Station("Ministry Of Trance RETRO Radio", "https://stream-164.zeno.fm/ny3mspp5tm0uv", R.drawable.ministry_of_trance_retro_radio),
            Station("Living Word Radio", "https://stream-172.zeno.fm/vvv2h7asn18uv", R.drawable.living_word_radio),
            Station("Chill Zone", "https://stream-163.zeno.fm/sy0a26km2uhvv", R.drawable.chill_zone),
            Station("Ana Radio Channel", "https://stream-173.zeno.fm/qmftv0ud4uhvv", R.drawable.ana_radio_channel),
            Station("T8 Radio", "https://stream-172.zeno.fm/t073t8g9t68uv", R.drawable.t8_radio),
            Station("Polytoria Radio", "https://stream-165.zeno.fm/6ne0wu28vuhvv", R.drawable.shwe90),
            Station("karomeanchlorine FM", "https://stream-163.zeno.fm/dm7nzm5m5a0uv", R.drawable.karomeanchlorine_fm),
            Station("Mandalay FM", "https://edge.mixlr.com/channel/nmtev", R.drawable.mandalay_fm),
            Station("Carrot FM", "https://stream-169.zeno.fm/sbhdc0yqkprvv", R.drawable.carrot_fm)
        ).map { station ->
            station.isFavorite = sharedPreferences.getBoolean(station.name, false)
            station
        } as ArrayList<Station>

        viewPager = findViewById(R.id.view_pager)
        viewPager.adapter = ViewPagerAdapter(this)

        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "All Stations"
                1 -> "Favorites"
                else -> null
            }
        }.attach()

        val searchView = findViewById<SearchView>(R.id.search_view)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val fragments = supportFragmentManager.fragments
                fragments.forEach { fragment ->
                    if (fragment is StationListFragment) {
                        fragment.filter(newText ?: "")
                    }
                }
                return true
            }
        })

        miniPlayerContainer = findViewById(R.id.mini_player_container)
        miniPlayerStationName = findViewById(R.id.mini_player_station_name)
        miniPlayerPlayPause = findViewById(R.id.mini_player_play_pause)
        miniPlayerImage = findViewById(R.id.mini_player_image)

        miniPlayerPlayPause.setOnClickListener {
            if (PlaybackManager.isPlaying()) {
                PlaybackManager.getPlayer().pause()
            } else {
                PlaybackManager.getPlayer().play()
            }
        }

        miniPlayerContainer.setOnClickListener {
            val currentStation = PlaybackManager.getCurrentStation()
            if (currentStation != null) {
                val index = stations.indexOf(currentStation)
                if (index != -1) {
                    val intent = Intent(this, PlayerActivity::class.java).apply {
                        putExtra("STATION_INDEX", index)
                        putExtra("STATION_LIST", stations)
                    }
                    startActivity(intent)
                }
            }
        }

        PlaybackManager.addListener(playerListener)
        updateMiniPlayer()
    }

    fun getStations(): ArrayList<Station> = stations

    private fun updateMiniPlayer() {
        val currentStation = PlaybackManager.getCurrentStation()
        if (currentStation != null) {
            miniPlayerContainer.visibility = View.VISIBLE
            miniPlayerStationName.text = currentStation.name
            Glide.with(this)
                .load(currentStation.imageResId)
                .into(miniPlayerImage)
            miniPlayerPlayPause.setImageResource(
                if (PlaybackManager.isPlaying()) R.drawable.ic_pause else R.drawable.ic_play
            )
        } else {
            miniPlayerContainer.visibility = View.GONE
        }
    }

    fun refreshFragments() { // Changed from private to public
        Log.d("MainActivity", "Refreshing fragments")
        val fragments = supportFragmentManager.fragments
        fragments.forEach { fragment ->
            if (fragment is StationListFragment) {
                fragment.refresh()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("MainActivity", "onActivityResult called, resultCode: $resultCode")
        refreshFragments()
    }

    override fun onDestroy() {
        super.onDestroy()
        PlaybackManager.removeListener(playerListener)
        if (isFinishing) {
            PlaybackManager.release()
        }
    }
}