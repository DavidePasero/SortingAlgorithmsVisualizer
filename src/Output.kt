import javax.swing.*
import java.awt.*

class Output
{
    init
    {
        val frame = JFrame("Array sorting algorithms visualizer")
        val visualizer = Visualizer()
        frame.background = Color.WHITE
        frame.setSize(1080, 720)
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
        frame.setVisible(true)
        frame.add(visualizer)
    }
}