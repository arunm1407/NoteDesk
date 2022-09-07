package com.example.notedesk.presentation.attachmentPreview.listener

import com.example.notedesk.domain.util.storage.InternalStoragePhoto

interface AttachmentLisenter {

    fun onAttachmentClicked(internalStoragePhoto: InternalStoragePhoto)
    fun onDelete(internalStoragePhoto: InternalStoragePhoto, position: Int)

}