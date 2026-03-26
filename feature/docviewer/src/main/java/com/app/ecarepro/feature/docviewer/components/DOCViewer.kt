package com.app.ecarepro.feature.docviewer.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.app.ecarepro.core.domain.model.DocType
import com.app.ecarepro.designsystem.core.component.EcareProWebView
import com.app.ecarepro.designsystem.core.component.EcareProWebViewState
import com.app.ecarepro.designsystem.core.component.rememberEcareProWebViewState
import com.app.ecarepro.designsystem.core.theme.EcareProTheme

/**
 * Thin wrapper kept for backward-compatibility with existing call-sites in this
 * feature module.  All functionality is now provided by [EcareProWebView].
 *
 * Prefer using [EcareProWebView] directly in new screens.
 *
 * @param modifier   Compose modifier.
 * @param url        Direct URL of the document to display.
 * @param docType    Type of the document.  Auto-detected from [url] when `null`.
 * @param state      Optional [EcareProWebViewState] for back-press / reload control.
 */
@Composable
fun DOCViewer(
    modifier: Modifier = Modifier,
    url: String,
    docType: DocType,
    state: EcareProWebViewState = rememberEcareProWebViewState(),
) {
    EcareProWebView(
        url = url,
        modifier = modifier,
        docType = docType,
        state = state,
        enableDownload = true,
        enableFileUpload = false,
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "PDF Document Preview")
@Composable
private fun DocViewerPdfPreview() {
    EcareProTheme {
        DOCViewer(
            modifier = Modifier.fillMaxSize(),
            url = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
            docType = DocType.PDF,
        )
    }
}

@Preview(showBackground = true, name = "Word Document Preview")
@Composable
private fun DocViewerDocPreview() {
    EcareProTheme {
        DOCViewer(
            modifier = Modifier.fillMaxSize(),
            url = "https://example.com/sample.docx",
            docType = DocType.DOC,
        )
    }
}

@Preview(showBackground = true, name = "Spreadsheet Preview")
@Composable
private fun DocViewerSpreadsheetPreview() {
    EcareProTheme {
        DOCViewer(
            modifier = Modifier.fillMaxSize(),
            url = "https://example.com/report.xlsx",
            docType = DocType.SPREADSHEET,
        )
    }
}
