package com.example.version2.presentation.attachmentPreview.listener



interface AttachmentLisenter {

    fun onAttachmentClicked(name: String)
    fun onDelete(name: String, position: Int)

}