package com.noahaung.myanmarradio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val stations = listOf(
            Station("Friends FM", "https://stream-172.zeno.fm/rtt1xsez338uv", "https://images.unsplash.com/photo-1508700115892-2f576ad6f11e"),
            Station("City FM", "https://stream.zeno.fm/rq4ux25pyeruv", "https://images.unsplash.com/photo-1510915228340-29c85a43dcfe"),
            Station("Shwe Ayeyar FM", "https://stream.zeno.fm/gvyz1utf4uhvv", "https://images.unsplash.com/photo-1506748686214-e9df14d4d9d0"),
            Station("Shwe Hinthar", "https://stream-172.zeno.fm/pxn7p5cbm48uv", "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f"),
            Station("Mingalar Par Radio", "https://stream-175.zeno.fm/hnsabdeu7k0uv", "https://images.unsplash.com/photo-1470225620780-d91e7f8e5c90"),
            Station("Dhamma", "https://stream-176.zeno.fm/v3awsfmhnchvv", "https://images.unsplash.com/photo-1511671788563-13b9ddda0d8c"),
            Station("MyaYoungChi Radio", "https://stream-174.zeno.fm/ggjbzeqpkawvv", "https://images.unsplash.com/photo-1514327605112-b887c0e61c0a"),
            Station("Cherry FM", "https://stream-169.zeno.fm/4x8x3g06n48uv", "https://images.unsplash.com/photo-1506157786151-b8491531f063"),
            Station("Melody FM", "https://stream-175.zeno.fm/yajlk8od0tmuv", "https://images.unsplash.com/photo-1511379936541-1b6a1f3b6e8c"),
            Station("Nevermore Channel", "https://stream-176.zeno.fm/wh799nuwvchvv", "https://images.unsplash.com/photo-1484755564898-2a4b141fc0bb"),
            Station("ARAKAN FM", "https://stream-175.zeno.fm/p9k212xkp2zuv", "https://images.unsplash.com/photo-1499364615658-2d2e3b6e8e8f"),
            Station("The Room Yangon", "https://stream-172.zeno.fm/tfft925h5a0uv", "https://images.unsplash.com/photo-1508290115101-663e1c9f4e3b"),
            Station("MCOMMM", "https://stream-175.zeno.fm/u4w6fu3nhm8uv", "https://images.unsplash.com/photo-1512436991641-6745cdb1722f"),
            Station("Sri Ksetra", "https://stream-172.zeno.fm/pdrht65hgdtuv", "https://images.unsplash.com/photo-1507838153414-4e6a4c3f3f6e"),
            Station("Relax Zone", "https://stream-173.zeno.fm/ji4wajqtih7tv", "https://images.unsplash.com/photo-1519681393784-d120267933ba"),
            Station("Burma Revolution Radio", "https://stream-173.zeno.fm/00m4g8wr1p8uv", "https://images.unsplash.com/photo-1507525428034-b723cf961d3e"),
            Station("Advent FM", "https://stream-176.zeno.fm/tus18w05na0uv", "https://images.unsplash.com/photo-1519125323398-675f0f8d0f0c"),
            Station("Arakan FM", "https://stream-173.zeno.fm/uhuyx8fve48uv", "https://images.unsplash.com/photo-1506744038136-462738e4f3fb"),
            Station("Myanmar", "https://stream-173.zeno.fm/q9cw34n6a48uv", "https://images.unsplash.com/photo-1519985176271-adb1088fa94c"),
            Station("Heaven Life", "https://stream-169.zeno.fm/ka3qyytcabjuv", "https://images.unsplash.com/photo-1508739773434-c26b3d09e7f7"),
            Station("Karen Gospel Radio", "https://stream-171.zeno.fm/17ke6qyhtp8uv", "https://images.unsplash.com/photo-1515168833906-d1a1e4c2c7e7"),
            Station("Voice Sports News", "https://stream-174.zeno.fm/zbh110758a0uv", "https://images.unsplash.com/photo-1517649765261-7f2d6e6c8c2e"),
            Station("Shwe90", "https://stream-164.zeno.fm/awh41anxg7atv", "https://images.unsplash.com/photo-1501785886874-5f68b5a794b8"),
            Station("Myanmar Truckers FM", "https://stream-176.zeno.fm/gh0t0wugy38uv", "https://images.unsplash.com/photo-1519125323398-675f0f8d0f0c"),
            Station("Springtime", "https://stream-163.zeno.fm/9500n7ywaq8uv", "https://images.unsplash.com/photo-1512054502232-10a0a035d372"),
            Station("ABC Radio", "https://stream-176.zeno.fm/o2mneumproruv", "https://images.unsplash.com/photo-1514327605112-b887c0e61c0a"),
            Station("Jazz Live Radio", "https://stream-176.zeno.fm/2txc199hsp8uv", "https://images.unsplash.com/photo-1511671788563-13b9ddda0d8c"),
            Station("Dr.Snail Band", "https://stream-163.zeno.fm/irtxizbdrnctv", "https://images.unsplash.com/photo-1506157786151-b8491531f063"),
            Station("MCOMM Country Live Radio", "https://stream-171.zeno.fm/k3cms4d6wp8uv", "https://images.unsplash.com/photo-1470225620780-d91e7f8e5c90"),
            Station("Burma Tamil Community", "https://stream-173.zeno.fm/cbk2avkrzc9uv", "https://images.unsplash.com/photo-1506748686214-e9df14d4d9d0"),
            Station("Star-Thura", "https://stream-164.zeno.fm/qalothzu8hutv", "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f"),
            Station("Ministry Of Trance RETRO Radio", "https://stream-164.zeno.fm/ny3mspp5tm0uv", "https://images.unsplash.com/photo-1508700115892-2f576ad6f11e"),
            Station("Living Word Radio", "https://stream-172.zeno.fm/vvv2h7asn18uv", "https://images.unsplash.com/photo-1510915228340-29c85a43dcfe"),
            Station("Chill Zone", "https://stream-163.zeno.fm/sy0a26km2uhvv", "https://images.unsplash.com/photo-1508290115101-663e1c9f4e3b"),
            Station("Ana Radio Channel", "https://stream-173.zeno.fm/qmftv0ud4uhvv", "https://images.unsplash.com/photo-1512436991641-6745cdb1722f"),
            Station("T8 Radio", "https://stream-172.zeno.fm/t073t8g9t68uv", "https://images.unsplash.com/photo-1507838153414-4e6a4c3f3f6e"),
            Station("Polytoria Radio", "https://stream-165.zeno.fm/6ne0wu28vuhvv", "https://images.unsplash.com/photo-1519681393784-d120267933ba"),
            Station("karomeanchlorine FM", "https://stream-163.zeno.fm/dm7nzm5m5a0uv", "https://images.unsplash.com/photo-1507525428034-b723cf961d3e"),
            Station("Mandalay FM", "https://edge.mixlr.com/channel/nmtev", "https://images.unsplash.com/photo-1519125323398-675f0f8d0f0c"),
            Station("Carrot FM", "https://stream-169.zeno.fm/sbhdc0yqkprvv", "https://images.unsplash.com/photo-1506744038136-462738e4f3fb")
        )

        val recyclerView = findViewById<RecyclerView>(R.id.station_list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = StationAdapter(stations) { station ->
            val index = stations.indexOf(station)
            val intent = Intent(this, PlayerActivity::class.java).apply {
                putExtra("STATION_INDEX", index)
                putExtra("STATION_LIST", ArrayList(stations)) // Use ArrayList for serialization
            }
            startActivity(intent)
        }
    }
}