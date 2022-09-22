package com.example.notedesk.presentation.attachmentPreview.listener



interface AttachmentLisenter {

    fun onAttachmentClicked(name: String)
    fun onDelete(name: String, position: Int)

}