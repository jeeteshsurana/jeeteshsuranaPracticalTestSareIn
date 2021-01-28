package com.example.basicstructurecoroutine.core.util

import java.io.IOException

/**
 * Created by JeeteshSurana.
 */
class ApiException(message: String,var code: Int) : IOException(message)
