package aop.part3.chapter03

data class AlarmDisplayModel(
    val hour: Int,
    val minute: Int,
    var onOff: Boolean
) {

    val onOffText: String
        get() {
            return if(onOff)"알람 끄기" else "알람 켜기"
        }

    val timeText: String
        get() {
            val h = "%02d".format(if (hour < 12) hour else hour - 12)
            val m = "%02d".format(minute)

            return "$h:$m"
        }

    val amPmText: String
        get() {
            return if (hour < 13) "AM" else "PM"
        }

    fun getDataForDB(): String {
        return "$hour:$minute"
    }
}
