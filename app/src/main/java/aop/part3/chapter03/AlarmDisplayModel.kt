package aop.part3.chapter03

data class AlarmDisplayModel(
    val hour: Int,
    val minute: Int,
    var onOff: Boolean
) {

    val timeText: String
        get() {
            val _hour = "%02d".format(if (hour < 12) hour else hour - 12)
            val _min = "m".format(minute)

            return "$_hour:$_min"
        }

    val amPmText: String
        get() {
            return if (hour < 13) "AM" else "PM"
        }

    fun getDataForDB(): String {
        return "$hour:$minute"
    }
}
