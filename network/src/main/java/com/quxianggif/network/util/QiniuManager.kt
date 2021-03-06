/*
 * Copyright (C) guolin, Suzhou Quxiang Inc. Open source codes for study only.
 * Do not use for commercial purpose.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.quxianggif.network.util

import com.qiniu.android.http.ResponseInfo
import com.qiniu.android.storage.UpCancellationSignal
import com.qiniu.android.storage.UpProgressHandler
import com.qiniu.android.storage.UploadManager
import com.qiniu.android.storage.UploadOptions

/**
 * 用于进行七牛云上传操作的管理类。
 *
 * @author guolin
 * @since 17/3/5
 */
object QiniuManager {

    private var uploadManager: UploadManager = UploadManager()

    private var isCanceled: Boolean = false

    fun upload(filePath: String, key: String, uptoken: String, listener: UploadListener?) {
        uploadManager.put(filePath, key, uptoken, { apiKey, info, _ ->
            if (listener != null && info != null) {
                if (info.isOK) {
                    listener.onSuccess(apiKey)
                } else {
                    listener.onFailure(info)
                }
            }
        }, UploadOptions(null, null, false, UpProgressHandler { _, percent ->
            listener?.onProgress(percent)
        }, UpCancellationSignal { isCanceled }))
    }


    fun cancel() {
        isCanceled = true
    }

    interface UploadListener {

        fun onSuccess(key: String)

        fun onFailure(info: ResponseInfo?)

        fun onProgress(percent: Double)

    }
}