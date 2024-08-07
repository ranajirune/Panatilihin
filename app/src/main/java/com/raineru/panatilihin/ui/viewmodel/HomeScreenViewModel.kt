package com.raineru.panatilihin.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raineru.panatilihin.data.Label
import com.raineru.panatilihin.data.LabelRepository
import com.raineru.panatilihin.data.Note
import com.raineru.panatilihin.data.NoteLabels
import com.raineru.panatilihin.data.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val labelRepository: LabelRepository
) : ViewModel() {

//    val showDropdownMenu: MutableState<Boolean> = mutableStateOf(false)

    /*private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()*/

//    private val _notes = MutableStateFlow<List<Note>>(emptyList())
//    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    var draftNoteTitle by mutableStateOf("")
        private set
    private val draftNoteTitleFlow = snapshotFlow { draftNoteTitle }

    var draftNoteContent by mutableStateOf("")
        private set
    private val draftNoteContentFlow = snapshotFlow { draftNoteContent }

    private val _draftNoteLabels = MutableStateFlow<List<Long>>(emptyList())
    val draftNoteLabels: StateFlow<List<Long>> = _draftNoteLabels.asStateFlow()

    private val hasDraftedNote = MutableStateFlow(false)

    val hasEmptyNote: StateFlow<Boolean> =
        combine(
            hasDraftedNote,
            draftNoteTitleFlow,
            draftNoteContentFlow
        ) { hasDraftedNote, draftNoteTitleFlow, draftNoteContentFlow ->
            hasDraftedNote
                    && draftNoteTitleFlow.isBlank()
                    && draftNoteContentFlow.isBlank()
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = false
            )

    val notes: StateFlow<List<NoteLabels>> = noteRepository.getNoteLabels()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val labels: StateFlow<List<Label>> = labelRepository.getAllLabels()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    var query: String by mutableStateOf("")
        private set
//    private val queryFlow = snapshotFlow { query }

    /*fun updateQuery(query: String) {
        this.query = query
    }*/

    fun deleteDraftNote() {
        draftNoteTitle = ""
        draftNoteContent = ""
        _draftNoteLabels.update { emptyList() }
        hasDraftedNote.update { false }
    }

    private val _selectedNotes = mutableStateListOf<Long>()
    val selectedNotes: List<Long> = _selectedNotes

    private fun toggleNodeSelection(noteId: Long) {
        if (_selectedNotes.contains(noteId)) {
            _selectedNotes.remove(noteId)
        } else {
            _selectedNotes.add(noteId)
        }
    }

    fun cancelSelection() {
        _selectedNotes.clear()
    }

    fun noteClicked(noteId: Long) {
        if (isInSelectionMode()) noteLongPressed(noteId)
    }

    fun noteLongPressed(noteId: Long) {
        toggleNodeSelection(noteId)
    }

    fun isInSelectionMode(): Boolean {
        return _selectedNotes.isNotEmpty()
    }

    fun updateDraftNoteTitle(title: String) {
        draftNoteTitle = title
    }

    fun updateDraftNoteContent(content: String) {
        draftNoteContent = content
    }

    fun draftNote() {
        hasDraftedNote.update {
            draftNoteTitle = ""
            draftNoteContent = ""
            _draftNoteLabels.update { emptyList() }
            true
        }
    }

    fun updateDraftNoteLabel(id: Long, checked: Boolean) {
        val mutableList = _draftNoteLabels.value.toMutableList()

        if (checked) {
            mutableList.add(id)
        } else {
            mutableList.remove(id)
        }

        _draftNoteLabels.update { mutableList }
    }

    suspend fun createLabel(label: Label): Long {
        return labelRepository.insertLabel(label)
    }

    fun writeToDiskValidDraftNote() {
        if (hasDraftedNote.value && !hasEmptyNote.value) {
            hasDraftedNote.update { false }

            viewModelScope.launch {
                val title = draftNoteTitle
                val content = draftNoteContent
                val labels = draftNoteLabels.value
                noteRepository.insertNote(
                    NoteLabels(
                        note = Note(
                            title = title,
                            content = content
                        ),
                        labels = labels.map {
                            Label(
                                name = "",
                                id = it
                            )
                        }
                    )
                )
                deleteDraftNote()
            }
        }
    }

    fun deleteSelectedNotes() {
        viewModelScope.launch {
            val selectedNotes = _selectedNotes.map {
                Note(title = "", content = "", id = it)
            }.toTypedArray()
            noteRepository.deleteNotes(*selectedNotes)
            _selectedNotes.clear()
        }
    }
}