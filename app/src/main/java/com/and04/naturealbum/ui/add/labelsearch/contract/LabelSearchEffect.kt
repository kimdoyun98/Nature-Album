package com.and04.naturealbum.ui.add.labelsearch.contract

sealed interface LabelSearchEffect {
    data object LabelSelected : LabelSearchEffect

    data class ToastMassage(val massage: LabelSelectEffectMassage) : LabelSearchEffect
}

enum class LabelSelectEffectMassage(val text: String) {
    EMPTY("라벨을 입력하세요."),
    USED("이미 존재하는 라벨입니다.")
}
