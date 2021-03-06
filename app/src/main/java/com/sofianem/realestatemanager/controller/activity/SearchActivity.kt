package com.sofianem.realestatemanager.controller.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.format.Time
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import com.appyvet.materialrangebar.RangeBar
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.sofianem.realestatemanager.R
import com.sofianem.realestatemanager.utils.Utils
import com.sofianem.realestatemanager.viewmodel.MyViewModel
import kotlinx.android.synthetic.main.activity_search.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt


@Suppress("DEPRECATION")
class SearchActivity : AppCompatActivity(), LifecycleOwner  {
    private val mMyViewModel by viewModel<MyViewModel>()
    private val mListIdForAdress: ArrayList<Int> = ArrayList()

    //ADDRESS
    var mAddress: String? = ""

    // PRICE
    var mPriceMini = -1  ; var mPriceMax = 999999999
    // SURFACE
    var mSurfaceMini = -1 ; var mSurfaceMax = 999999
    // NB ROOM
    var mRoomMini = -1  ; var mRoomMax = 99
    // NB PHOTO
    var mPhotoMini: Int = -1  ; var mPhotoMax: Int = 99
    // DATE CREATE
    private var mCreateDateBegin: Long = 1  ; private var mCreateDateEnd: Long = 88888888870000
    // DATE END
    private var mSoldDateBegin: Long = 1  ; private var mSoldDateEnd: Long = 88888888870000
    // ALL
    var mType: String? = "%" ; var mStatus: String = "%" ; var mPerson: String? = "%"
    // LOCATION
    private var mPark: String? = "%" ; private var mPharmacy: String? = "%"  ; private var mSchool: String? = "%"  ; private var mMarket: String? = "%"

    // LOCATION
    var mLocation: String? = "%"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       val a =  checkIsTablet()
        if (a)
        {    setContentView(R.layout.activity_search)}
        else
        {  setContentView(R.layout.activity_search_2)}


        mMyViewModel.mAllEstateId.observe(this, androidx.lifecycle.Observer {mList ->
            mList.forEach {mID -> mListIdForAdress.add(mID) } })

        initSchool()
        searchPerson() ; searchRoom() ; searchType()
        searchSurface() ; searchPrice() ; searchAddress()
       // searchStatus()
        initDateCreate() ; initDateSold()
        initMarket()
        initPark()
        initPharmacy()

        initPhoto()
        initSearch()
        onClickHome()  }
/*
    private fun searchStatus() {
        search_switch_sold.setOnCheckedChangeListener { c, _ ->
            if (c.isChecked) {
                a_search_sold_dateEnd.visibility = View.VISIBLE
                a_search_sold_dateBegin.visibility = View.VISIBLE
                txtSoldDate1.visibility = View.VISIBLE
                txtSoldDate2.visibility = View.VISIBLE
                mStatus = "sold"
                initSearch()}

            if (!c.isChecked) {
                a_search_sold_dateEnd.visibility = View.INVISIBLE
                a_search_sold_dateBegin.visibility = View.INVISIBLE
                txtSoldDate1.visibility = View.INVISIBLE
                txtSoldDate2.visibility = View.INVISIBLE
                mStatus = "%"
                mSoldDateBegin = 1
                mSoldDateEnd = 88888888870000
                initSearch()} } }

 */


    private fun searchAddress() {
        var mStreetNumber = ""
        var mStreetName = ""
        var mCity = ""
        if (!Places.isInitialized()) { Places.initialize(applicationContext, "AIzaSyByK0jz-yxjpZFX88W8zjzTwtzMtkPYC4w") }

        val autocompleteFragment = Utils.configureAutoCompleteFrag(supportFragmentManager, resources, this, "Adress")
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                a_search_ed_adress.text = place.address
                place.addressComponents?.asList()?.forEach { mAdressComp ->
                    Log.i("TAG", "AutoComplet: " + mAdressComp.types + " " )
                    when {
                        mAdressComp.types.contains("street_number") -> { mStreetNumber = mAdressComp.name }
                        mAdressComp.types.contains("route") -> { mStreetName = mAdressComp.name }
                        mAdressComp.types.contains("locality") -> { mCity = mAdressComp.name }
                    }
                }

                val mDisplayAdress = "$mStreetNumber $mStreetName, $mCity "
                a_search_ed_adress.text = mDisplayAdress
                a_search_ed_adress.visibility = View.VISIBLE
                mAddress = "$mStreetNumber $mStreetName"

                mLocation = Utils.mLatLngString(place.latLng)
                checkDistance(mLocation!!)
            }
            override fun onError(status: Status) { Log.i("TAG", "An error occurred: $mStatus")
            } }) }

    private fun checkDistance(mGeoLocSearch: String) {
        mMyViewModel.mAllEstateId.observe(this, androidx.lifecycle.Observer { mListID ->
            mListIdForAdress.clear()
            mListID?.forEach { mId ->
                val mGeoLocItem =  mMyViewModel.getGeoLocById(mId)
                val mSearchLat = Utils.currentLat(mGeoLocSearch)
                val mSearchLng = Utils.currentLng(mGeoLocSearch)
                val mItemLat = Utils.currentLat(mGeoLocItem)
                val mItemLng = Utils.currentLng(mGeoLocItem)

                val mDistance = Utils.calculateDistance(mSearchLat, mSearchLng, mItemLat, mItemLng).roundToInt()
                if (mDistance < 750) {mListIdForAdress.add(mId)}

                println( " ------>>>>>> DISTANCE <<<<<------ / ID /  $mId  /   $mDistance ")
            } // FOREACH
            initSearch()
        })



    }


    private fun searchSurface() {
        a_search_rangebar_surface.setOnRangeBarChangeListener(object : RangeBar.OnRangeBarChangeListener {
            override fun onTouchEnded(rangeBar: RangeBar?) {}
            override fun onRangeChangeListener(rangeBar: RangeBar?, leftPinIndex: Int, rightPinIndex: Int, leftPinValue: String, rightPinValue: String) {
             val mDisplayMiniSurface ="$leftPinValue  Sq/ft"
                val mDisplayMaxSurface = "$rightPinValue  Sq/ft"
                a_search_tx_minisurface.text = mDisplayMiniSurface
                a_search_tx_maxsurface.text = mDisplayMaxSurface
                mSurfaceMini = leftPinValue.toInt() ; mSurfaceMax = rightPinValue.toInt()
                initSearch()}

            override fun onTouchStarted(rangeBar: RangeBar?) {} }) }


    private fun searchRoom() {
        a_search_ed_room_rangeBar.setOnRangeBarChangeListener(object :
            RangeBar.OnRangeBarChangeListener {
            override fun onTouchEnded(rangeBar: RangeBar?) {}
            override fun onRangeChangeListener(rangeBar: RangeBar?, leftPinIndex: Int, rightPinIndex: Int, leftPinValue: String, rightPinValue: String) {
                a_search_ed_room_mini.text = leftPinValue ; a_search_ed_room_max.text = rightPinValue
                mRoomMini = leftPinValue.toInt() ; mRoomMax = rightPinValue.toInt()
                if (mRoomMini == 1 && mRoomMax == 12)
                {  mRoomMini = -1; mRoomMax = 99 }
                initSearch()}

            override fun onTouchStarted(rangeBar: RangeBar?) {} }) }

    private fun searchPerson() {
        val mSelectPersonn = resources.getStringArray(((R.array.Person)))
        if (spPersonn != null) { spPersonn.background.setColorFilter(resources.getColor(R.color.colorD), PorterDuff.Mode.SRC_ATOP)
            val adapter = ArrayAdapter(this, R.layout.spinner_custom, mSelectPersonn)
            spPersonn.adapter = adapter}

        spPersonn.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, i: Int, id: Long) {
                if (mSelectPersonn[i] == "Person") {mPerson = "%";   initSearch() }
                else { mPerson = mSelectPersonn[i]; initSearch()} }

            override fun onNothingSelected(parent: AdapterView<*>) {} } }


    private fun searchType() {
        val mSelectType = resources.getStringArray(((R.array.Type)))
        if (spType != null) { spType.background.setColorFilter(resources.getColor(R.color.colorD), PorterDuff.Mode.SRC_ATOP)
            val adapter = ArrayAdapter(this, R.layout.spinner_custom, mSelectType)
            spType.adapter = adapter}

        spType.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, i: Int, id: Long) {
                if (mSelectType[i] == "Type") {mType = "%";   initSearch()}
                else { mType = mSelectType[i]; initSearch() } }

            override fun onNothingSelected(parent: AdapterView<*>) {} } }


    private fun searchPrice() {
        a_search_rangebar_price.setOnRangeBarChangeListener(object :
            RangeBar.OnRangeBarChangeListener {
            override fun onTouchEnded(rangeBar: RangeBar?) {}
            override fun onTouchStarted(rangeBar: RangeBar?) {}
            override fun onRangeChangeListener(rangeBar: RangeBar?, leftPinIndex: Int, rightPinIndex: Int, leftPinValue: String?, rightPinValue: String?) {
                val valueLeft = leftPinValue.toString() + "0000"  ; val displayValueLeft = Utils.addWhiteSpace(valueLeft)
                val valueRight = rightPinValue.toString() + "0000"  ; val displayValueRight = Utils.addWhiteSpace(valueRight)
                val mDisplayMiniPrice = "$displayValueLeft  $"
                val mDisplayMaxPrice =  "$displayValueRight  $"

                a_search_tx_pricemini.text = mDisplayMiniPrice
                a_search_tx_pricemax.text = mDisplayMaxPrice
                mPriceMini = valueLeft.toInt() ; mPriceMax = valueRight.toInt()
                if (mPriceMini == 100000 && mPriceMax == 3000000)
                {  mPriceMini = -1 ; mPriceMax = 999999999 }
                initSearch()} }) }

    private fun initPhoto() {
        a_search_ed_photo_rangebar.setOnRangeBarChangeListener(object :
            RangeBar.OnRangeBarChangeListener {
            override fun onTouchEnded(rangeBar: RangeBar?) {}
            override fun onRangeChangeListener(rangeBar: RangeBar?, leftPinIndex: Int, rightPinIndex: Int, leftPinValue: String?, rightPinValue: String?
            ) { a_search_ed_photo_mini.text = leftPinValue.toString(); a_search_ed_photo_max.text = rightPinValue.toString()
                mPhotoMini = leftPinValue!!.toInt() ; mPhotoMax = rightPinValue!!.toInt()
                if (mPhotoMini == 0 && mPhotoMax == 19)
                {  mPhotoMini = -1 ; mPhotoMax = 99 }
                initSearch()}

            override fun onTouchStarted(rangeBar: RangeBar?) {} }) }

    private fun initMarket() { a_search_cb_market.setOnCheckedChangeListener { _, b ->
        if (b){ mMarket = "ok" ;  initSearch()}
        else { mMarket = "%" ;  initSearch()} }}

    private fun initSchool() { a_search_cb_school.setOnCheckedChangeListener { _, b ->
        if (b){ mMarket = "ok" ;  initSearch()}
        else { mMarket = "%" ;  initSearch()} }}

    private fun initPharmacy() { a_search_cb_pharmacy.setOnCheckedChangeListener { _, b ->
        if (b){ mMarket = "ok" ;  initSearch()}
        else { mMarket = "%" ;  initSearch()} }}

    private fun initPark() { a_search_cb_park.setOnCheckedChangeListener { _, b ->
        if (b){ mMarket = "ok" ;  initSearch()}
        else { mMarket = "%" ;  initSearch()} }}

    private fun initDateSold() {
        a_search_sold_dateBegin.setOnClickListener {
            mSoldDateBegin = 1
            val dpd = OnDateSetListener { _, y, m, d -> a_search_sold_dateBegin.text = Utils.formatDate(y,m,d)
                mSoldDateBegin = Utils.convertToEpoch(Utils.formatDate(y,m,d))
                initSearch()}
            val now = Time()
            now.setToNow()
            val datePicker = DatePickerDialog(this, R.style.MyAppThemeCalendar, dpd, now.year, now.month, now.monthDay)
            datePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.colorB))
            datePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.colorB))
            datePicker.show()

            cancelDSB.visibility = View.VISIBLE
        }

        cancelDSB.setOnClickListener {
            println("-------C ------- DEC")
            a_search_sold_dateBegin.text = getString(R.string.date)
            mSoldDateBegin = 1
            cancelDSB.visibility = View.GONE
            initSearch()
        }


        a_search_sold_dateEnd.setOnClickListener {
            mSoldDateEnd = Utils.formatDateV2(Date())
            val dpd = OnDateSetListener { _, y, m, d -> a_search_sold_dateEnd.text = Utils.formatDate(y,m,d)
                mSoldDateEnd = Utils.convertToEpoch(Utils.formatDate(y,m,d))
                initSearch()}
            val now = Time()
            now.setToNow()
            val datePicker = DatePickerDialog(this, R.style.MyAppThemeCalendar, dpd, now.year, now.month, now.monthDay)
            datePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.colorB))
            datePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.colorB))

            datePicker.show()

            cancelDSE.visibility = View.VISIBLE
        }
        cancelDSE.setOnClickListener {
            println("-------C ------- DBC")
            a_search_sold_dateEnd.text = getString(R.string.dateplus)
            mSoldDateEnd = 88888888870000
            cancelDSE.visibility = View.GONE
            initSearch()
        }}

    private fun initDateCreate() {
        a_search_ed_dateBegin_create.setOnClickListener {
            val dpd = OnDateSetListener { _, y, m, d ->
                a_search_ed_dateBegin_create.text = Utils.formatDate(y, m, d)
                mCreateDateBegin = Utils.convertToEpoch(Utils.formatDate(y, m, d))
                initSearch()}
            val now = Time()
            now.setToNow()
            val datePicker = DatePickerDialog(this, R.style.MyAppThemeCalendar, dpd, now.year, now.month, now.monthDay)
            datePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.colorB))
            datePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.colorB))
            datePicker.show()
            cancelDCB.visibility = View.VISIBLE
        }
        cancelDCB.setOnClickListener {
            a_search_ed_dateBegin_create.text = getString(R.string.date)
            mCreateDateBegin = 1
            cancelDCB.visibility = View.GONE
            initSearch()
        }


        a_search_ed_dateEnd_create.setOnClickListener {
            mCreateDateEnd = Utils.formatDateV2(Date())
            val dpd = OnDateSetListener { _, y, m, d ->
                a_search_ed_dateEnd_create.text = Utils.formatDate(y,m,d)
                mCreateDateEnd = Utils.convertToEpoch(Utils.formatDate(y,m,d))
                initSearch()}
            val now = Time()
            now.setToNow()
            val datePicker = DatePickerDialog(this, R.style.MyAppThemeCalendar, dpd, now.year, now.month, now.monthDay)
            datePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.colorB))
            datePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.colorB))

            datePicker.show()

            cancelDCE.visibility = View.VISIBLE
        }
        cancelDCE.setOnClickListener {
            a_search_ed_dateEnd_create.text = getString(R.string.dateplus)
            mCreateDateEnd = 88888888870000
            cancelDCE.visibility = View.GONE
            initSearch()}
    }



    private fun onClickHome() {
        a_search_fb_home.setOnClickListener { val intent = Intent(this, MainActivity::class.java)
            startActivity(intent) } }

    private fun initSearch() {
        mMyViewModel.getSearchAll(
            mPerson, mType,
            mSurfaceMini, mSurfaceMax,
            mPriceMini, mPriceMax,
            mRoomMini, mRoomMax,
            mCreateDateBegin, mCreateDateEnd,
            mPhotoMini, mPhotoMax,
            mSoldDateBegin, mSoldDateEnd,
            mStatus,
            mPharmacy, mSchool, mMarket, mPark).observe(this, androidx.lifecycle.Observer {searchList ->


            val mListId = arrayListOf<Int>()
            searchList.forEach { mListId.add(it) }

            val mSearchlist =  listsEqual(mListId, mListIdForAdress)
            a_search_txt_item.text = mSearchlist.size.toString()

            clickSearch.setOnClickListener { loadRV(mSearchlist)}


        })}

    private fun listsEqual(mListId: ArrayList<Int>, mListIdForAdress: ArrayList<Int>): ArrayList<Int> {
        val mDifference = mListId.minus(mListIdForAdress)
        mDifference.forEach { mListId.remove(it) }

        return mListId
    }

    private fun loadRV(mSearchlist: ArrayList<Int>) {


        if (mSearchlist.isNullOrEmpty()) {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent) }
        else { val intent = Intent(this, MainActivity::class.java)
            mSearchlist.sort()
            intent.putExtra(MASTER_ID, mSearchlist)
            println(" ------List Id------$mSearchlist")
            startActivity(intent) } }


    companion object {
        const val MASTER_ID = "master_id"
    }


    private fun checkIsTablet(): Boolean {
        val display: Display = (this as Activity).windowManager.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        val widthInches: Float = metrics.widthPixels / metrics.xdpi
        val heightInches: Float = metrics.heightPixels / metrics.ydpi
        val diagonalInches = sqrt(
            widthInches.toDouble().pow(2.0) + heightInches.toDouble().pow(2.0)
        )
        return diagonalInches >= 7.0
    }
}
