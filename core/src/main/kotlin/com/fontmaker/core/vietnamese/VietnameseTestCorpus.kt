package com.fontmaker.core.vietnamese

object VietnameseTestCorpus {
    val BASIC_VOWELS = listOf(
        "a", "ă", "â", "e", "ê", "i", "o", "ô", "ơ", "u", "ư", "y"
    )

    val TONE_SERIES = listOf(
        listOf("á", "à", "ả", "ã", "ạ"),
        listOf("ắ", "ằ", "ẳ", "ẵ", "ặ"),
        listOf("ấ", "ầ", "ẩ", "ẫ", "ậ"),
        listOf("é", "è", "ẻ", "ẽ", "ẹ"),
        listOf("ế", "ề", "ể", "ễ", "ệ"),
        listOf("í", "ì", "ỉ", "ĩ", "ị"),
        listOf("ó", "ò", "ỏ", "õ", "ọ"),
        listOf("ố", "ồ", "ổ", "ỗ", "ộ"),
        listOf("ớ", "ờ", "ở", "ỡ", "ợ"),
        listOf("ú", "ù", "ủ", "ũ", "ụ"),
        listOf("ứ", "ừ", "ử", "ữ", "ự"),
        listOf("ý", "ỳ", "ỷ", "ỹ", "ỵ")
    )

    val REAL_SENTENCES = listOf(
        "Tiếng Việt có đầy đủ dấu thanh và chữ cái đặc biệt.",
        "Tôi đang tạo một bộ phông chữ viết tay của riêng mình.",
        "Ăn quả nhớ kẻ trồng cây.",
        "Trường đại học, kỹ thuật, ứng dụng và phát triển phần mềm."
    )
}
