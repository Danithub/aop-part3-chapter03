package aop.part3.chapter03

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        InitOnOffAlarmButton()

        InitChangeAlarmTimeButton()

        val model = fetchDataFromSharedPreference()
        renderView(model)
    }

    private fun InitOnOffAlarmButton() {
        val onOffAlarmButton = findViewById<Button>(R.id.onOffAlarmButton)
        onOffAlarmButton.setOnClickListener {
            val model = it.tag as? AlarmDisplayModel ?: return@setOnClickListener
            val newModel = SaveAlarmDisplayModel( model.hour, model.minute, model.onOff.not())
            renderView(newModel)

            if(newModel.onOff){
                val calendar = Calendar.getInstance().apply{
                    set(Calendar.HOUR_OF_DAY, newModel.hour)
                    set(Calendar.MINUTE, newModel.minute)

                    if(before(Calendar.getInstance())){
                        add(Calendar.DATE, 1)
                    }
                }

                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(this, AlarmReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE
                    , intent, PendingIntent.FLAG_UPDATE_CURRENT)

                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            }else{
                cancelAlarm()
            }
        }
    }

    private fun InitChangeAlarmTimeButton() {
        val changeAlarmTimeButton = findViewById<Button>(R.id.changeAlarmTimeButton)
        changeAlarmTimeButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(this, {view, hour, minute ->
                val model = SaveAlarmDisplayModel(hour, minute, false)
                renderView(model)
                cancelAlarm()
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false)
                .show()
        }
    }

    private fun SaveAlarmDisplayModel(
        _hour: Int,
        _minute: Int,
        _onOff: Boolean
    ): AlarmDisplayModel {

        val model = AlarmDisplayModel(
            hour = _hour,
            minute = _minute,
            onOff = _onOff
        )

        val sharedPreference = getSharedPreferences(SHARED_PREFERENCE_NAME, MODE_PRIVATE)

        with(sharedPreference.edit()){
            putString(ALARM_KEY, model.getDataForDB())
            putBoolean(ONOFF_KEY, model.onOff)
            commit()
        }

        return model
    }


    private fun fetchDataFromSharedPreference(): AlarmDisplayModel {
        val sharedPreference = getSharedPreferences(SHARED_PREFERENCE_NAME, MODE_PRIVATE)
        val timeDBValue = sharedPreference.getString(ALARM_KEY, "12:00") ?: "12:00"
        val onOff = sharedPreference.getBoolean(ONOFF_KEY,false)

        val time = timeDBValue.split(':')
        val hour = time[0].toInt()
        val min = time[1].toInt()

        val model = AlarmDisplayModel(hour, min, onOff)

        //보정 예외처리
        val pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, Intent(this, AlarmReceiver::class.java), PendingIntent.FLAG_NO_CREATE)

        if((pendingIntent == null) and model.onOff){
            //알람은 꺼져있는데, 데이터는 켜져있는 경우
            model.onOff = false
        }else if((pendingIntent != null) and model.onOff.not()){
            //알람은 켜져있는데, 데이터는 꺼져있는 경우
            pendingIntent.cancel()
        }

        return model
    }

    private fun cancelAlarm() {
        val pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, Intent(this, AlarmReceiver::class.java), PendingIntent.FLAG_NO_CREATE)
        pendingIntent?.cancel()
    }

    companion object{
        private const val ALARM_REQUEST_CODE = 1000
        private const val SHARED_PREFERENCE_NAME = "time"
        private const val ALARM_KEY = "alarm"
        private const val ONOFF_KEY = "onOff"
    }

    private fun renderView(model: AlarmDisplayModel) {
        findViewById<TextView>(R.id.alarmTimeTextView).apply{
            text = model.timeText
        }

        findViewById<TextView>(R.id.amPmTextView).apply{
            text = model.amPmText
        }

        findViewById<Button>(R.id.onOffAlarmButton).apply{
            text = model.onOffText
            tag = model
        }
    }

}