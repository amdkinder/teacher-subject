package uz.devcraft.domain

data class RestResult<T>(
    var data: T? = null,
    var message: String? = null,
    var success: Boolean? = null
) {
    companion object {
        fun <T> success(data: T): RestResult<T> {
            return RestResult(success = true, data = data)
        }

        fun <T> fail(message: String): RestResult<T> {
            return RestResult(success = false, message = message)
        }
    }
}
