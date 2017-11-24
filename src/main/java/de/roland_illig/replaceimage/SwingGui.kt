package de.roland_illig.replaceimage

import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.image.BufferedImage
import java.io.File
import java.util.prefs.Preferences
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JFileChooser
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.WindowConstants
import javax.swing.filechooser.FileNameExtensionFilter

fun main(args: Array<String>) {
    SwingGui().run()
}

class SwingGui {
    private val preferencesNode = Preferences.userNodeForPackage(SwingGui::class.java)

    private val window = JFrame("Image Replacer")
    private val preview = JLabel()

    private var source = RGBA(640, 480)
    private var replace = RGBA(0, 0)
    private var with = RGBA(0, 0)

    private fun setPreviewImage() {
        val target = RGBA(source.width, source.height)
        target.draw(0, 0, source)
        target.replace(replace, with)

        preview.icon = ImageIcon(target.toBufferedImage())
        window.pack()
    }

    fun run() {
        val sourceButton = newOpenButton("Source", { img -> source = img })
        val replaceButton = newOpenButton("Replace", { img -> replace = img })
        val withButton = newOpenButton("With", { img -> with = img })
        val saveButton = newSaveButton("Save")

        val buttonBar = JPanel()
        buttonBar.layout = FlowLayout(FlowLayout.LEADING)
        buttonBar.add(sourceButton)
        buttonBar.add(replaceButton)
        buttonBar.add(withButton)
        buttonBar.add(saveButton)

        setPreviewImage()

        val cp = window.contentPane
        cp.layout = BorderLayout()
        cp.add(buttonBar, BorderLayout.PAGE_START)
        cp.add(preview)

        window.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
        window.pack()
        window.setLocationRelativeTo(null)
        window.isVisible = true
    }

    private fun newOpenButton(label: String, action: (RGBA) -> Unit): JComponent {
        val button = JButton(label)
        button.addActionListener {
            showFileChooser(false) {
                val img = ImageIO.read(it)
                action(RGBA(img))
                setPreviewImage()
            }
        }
        return button
    }

    private fun newSaveButton(label: String): JButton {
        val button = JButton(label)
        button.addActionListener {
            showFileChooser(true) {
                ImageIO.write((preview.icon as ImageIcon).image as BufferedImage, it.extension, it)
            }
        }
        return button
    }

    private fun showFileChooser(save: Boolean, action: (File) -> Unit) {
        val label = if (save) "Save" else "Open"

        val chooser = JFileChooser()
        chooser.dialogTitle = label
        if (save) {
            chooser.dialogType = JFileChooser.SAVE_DIALOG
        }
        chooser.fileFilter = FileNameExtensionFilter("Images", "png", "jpg", "jpeg", "gif")
        chooser.currentDirectory = File(preferencesNode.get("lastDir", "."))

        if (chooser.showDialog(null, label) == JFileChooser.APPROVE_OPTION) {
            preferencesNode.put("lastDir", chooser.currentDirectory.toString())
            action(chooser.selectedFile)
        }
    }

}
