package aop.part3.chapter03

import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        InitOnOffAlarmButton()

        InitChangeAlarmTimeButton()
    }

    private fun InitOnOffAlarmButton() {
        val onOffAlarmButton = findViewById<Button>(R.id.onOffAlarmButton)
        onOffAlarmButton.setOnClickListener {

        }
    }

    private fun InitChangeAlarmTimeButton() {
        val changeAlarmTimeButton = findViewById<Button>(R.id.changeAlarmTimeButton)
        changeAlarmTimeButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(this, {view, hour, minute ->
                val model = SaveAlarmDisplayModel(hour, minute, false)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false)
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

        val sharedPreference = getSharedPreferences("time", MODE_PRIVATE)

        with(sharedPreference.edit()){
            putString("alarm", model.getDataForDB())
            putBoolean("onOff", model.onOff)
            commit()
        }

        return model
    }
}