import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.image.*
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.floor
import kotlin.test.assertFalse

class Visualizer(/*var img: BufferedImage, var g2: Graphics2D*/): JPanel(), ActionListener, KeyListener
{
    var quantiElementi = 550
    val tempoDiUpdateDefault = 5
    var tempoDiUpdate = tempoDiUpdateDefault
    var spessore = 550 / quantiElementi

    //Tutta la parte nuova del progetto, datata 30/05/2020
    var ordinamentoSelezionato = 5//indica quale ordinamento tra quelli presenti é stato selezionato
    var ordinamento = Ordinamento()//classe che contiene le funzioni di ordinamento che estende la classe thread, perché l'ordinamento viene eseguito in un thread a parte
    val canvas = Canvas()//canvas é la classe hce disegna sullo schermo e che fa l'update

    val sapo = ImageIO.read(File("Immagini" + File.separator + "science_park.png")) as BufferedImage//l'immagine in input
    lateinit var arraySapobully: Array<ImmagineNumero>//l'array dell'immagine

    val decidiElementi = JComboBox<Int>()
    val decidiRitardo = JTextField(2)
    val run = JButton("Run")
    val shuffle = JButton("Shuffle")
    val arrayNomeOrdinamenti = arrayOf("Bubble sort", "Cocktail Shaker", "Comb sort", "Selection sort", "Double-Selection", "Counting sort", "Merge sort", "Quick sort", "Insertion sort")
    val arrayOrdinamenti = Array<JButton>(arrayNomeOrdinamenti.size){i -> JButton()}//Contiene i pulsanti per selezionare l'ordinamento
    val confermaElementi = JButton("OK")

    init
    {
        this.background = Color.WHITE

        //abbiamo i gbc ed il settaggio del layout
        var gbc = GridBagConstraints()
        this.layout = GridBagLayout()

        var pannelloElementi = JPanel()
        pannelloElementi.background = Color.WHITE
        decidiRitardo.addKeyListener(this)
        decidiRitardo.text = "5"
        val arrayElementi = arrayOf(10, 25, 50, 110, 275, 550)
        for(i in 0..arrayElementi.lastIndex)
            decidiElementi.addItem(arrayElementi[i])
        decidiElementi.selectedItem = 550
        confermaElementi.actionCommand = "confermaElementi"
        confermaElementi.addActionListener(this)
        pannelloElementi.add(decidiRitardo)
        pannelloElementi.add(decidiElementi)
        pannelloElementi.add(confermaElementi)
        gbc.gridx = 0
        gbc.gridy = 0
        this.add(pannelloElementi, gbc)

        //pannelloCanvas é quello in alto a sinistra che contiene Canvas
        var pannelloCanvas = JPanel()
        pannelloCanvas.background = Color.WHITE
        canvas.preferredSize = Dimension(550, 350)
        pannelloCanvas.add(canvas)
        gbc.gridx = 0
        gbc.gridy = 1
        this.add(pannelloCanvas, gbc)

        //pannelloPulsanti contiene contiene il pulsante Run ed i dati tecnici tipo accesso all'array ecc.
        var pannelloControllo = JPanel()
        pannelloControllo.background = Color.WHITE

        run.isFocusPainted = false
        run.addActionListener(this)
        run.actionCommand = "run"
        pannelloControllo.add(run)

        shuffle.isFocusPainted = false
        shuffle.addActionListener(this)
        shuffle.actionCommand = "shuffle"
        pannelloControllo.add(shuffle)

        gbc.gridx = 0
        gbc.gridy = 2
        this.add(pannelloControllo, gbc)

        //pannelloOrdinamenti ti permette di scegliere quale ordinamento usare
        var pannelloOrdinamenti = JPanel()
        pannelloOrdinamenti.background = Color.WHITE
        val gridlayout = GridLayout(arrayNomeOrdinamenti.size, 1)
        gridlayout.vgap = 10
        pannelloOrdinamenti.layout = gridlayout
        for(i in 0 until arrayOrdinamenti.size)
        {
            arrayOrdinamenti[i].text = arrayNomeOrdinamenti[i]
            arrayOrdinamenti[i].actionCommand = "$i"
            arrayOrdinamenti[i].addActionListener {
                e ->
                run {
                    ordinamentoSelezionato = e?.actionCommand!!.toInt()
                }
            }
            pannelloOrdinamenti.add(arrayOrdinamenti[i])
        }

        var barra = JScrollPane(pannelloOrdinamenti)
        gbc.gridx = 1
        gbc.gridy = 1
        this.add(barra, gbc)

        generaArraySapo()
    }

    fun creaBarraAccesso(): BufferedImage//crea la barraAccesso, e cioé una BufferedImage di forma rettangolare di width=spessore e height = 350 di colore rosso con un alpha di 50
    {
        var barra = BufferedImage(spessore, 350, BufferedImage.TYPE_INT_ARGB)
        val g2: Graphics2D = barra.graphics as Graphics2D
        g2.setColor(Color(255, 0, 0, 255))
        g2.fillRect(0, 0, barra.width, barra.height)
        return barra
    }

    fun generaArraySapo()//divide l'immagine di input (ad ora quella di sapo) in tante piccole sottoimmagini di larghezza 550/quantiElementi
    {
        arraySapobully = arrayOf<ImmagineNumero>()
        var widthImmagine = 550 / quantiElementi
        var heightImmagine = 350
        for(i in 0 until quantiElementi)
        {
            var immagineNumero = ImmagineNumero(sapo.getSubimage(i * widthImmagine, 0, widthImmagine, heightImmagine), i)
            arraySapobully += immagineNumero
        }
        shuffleArraySapo()
    }

    fun shuffleArraySapo()//mescola l'arraySapo
    {
        var arrayimmagineNumeroVuoto = arrayOf<ImmagineNumero>()//e un ArrayList vuoto ma grande 110 elementi
        var numeri = ArrayList<Int>(quantiElementi)//un array che contiene i numeri da 1 a 110
        for(i in 0 until quantiElementi)
            numeri.add(i)

        for(i in 0 until quantiElementi)
        {
            var indice = Math.round((Math.random() * (numeri.size - 1))).toInt()
            var numero = numeri[indice]
            numeri.removeAt(indice)
            arrayimmagineNumeroVuoto += (arraySapobully[numero])
        }

        System.arraycopy(arrayimmagineNumeroVuoto, 0, arraySapobully, 0, quantiElementi)
        //canvas.update(0)
    }

    override fun actionPerformed(e: ActionEvent?)
    {
        when(e?.actionCommand)
        {
            "confermaElementi" ->
            {
                tempoDiUpdate = if(decidiRitardo.text.isEmpty())
                    tempoDiUpdateDefault
                else
                    decidiRitardo.text.toInt()
                quantiElementi = decidiElementi.selectedItem as Int
                spessore = 550 / quantiElementi
                generaArraySapo()
                canvas.update(0)
            }
            "run" ->
            {
                //in caso non li disabilitassimo, mentre si esegue il thread potrebbero essere premuti e potrebbero succedere questioni spiaceovli
                run.isEnabled = false
                shuffle.isEnabled = false
                confermaElementi.isEnabled = false
                ordinamento = Ordinamento()
                ordinamento.start()
            }
            "shuffle" ->
            {
                shuffleArraySapo()
                canvas.update(0)//mostriamo l'array disordinato con la barra accesso all'indice 0
            }
        }
    }

    fun stampaArray()
    {
        for(i in 0..arraySapobully.lastIndex)
        {
            println(arraySapobully[i].numero)
        }
    }

    override fun keyReleased(e: KeyEvent?) {
    }

    override fun keyPressed(e: KeyEvent?) {
    }

    override fun keyTyped(e: KeyEvent?) {
        if((e?.keyChar!!.isDigit()) && (decidiRitardo.text.length < 2)) {
        }
        else
            e.consume()
    }

    inner class Ordinamento: Thread()
    {
        var trueIndex = 0//serve agli algoritmi ricorsivi come Merge sort o Quick sort per accedere all'elemento corretto dell'array completo dai sottoarray ricorsivi

        override fun run()
        {
            when(ordinamentoSelezionato)
            {
                0 -> bubbleSort()
                1 -> coctailShakerSort()
                2 -> combSort()
                3 -> selectionSort()
                4 -> doubleSelectionSort()
                5 -> countingSort()
                6 -> mergeSort()
                7 -> quickSort()
                8 -> insertionSort()
            }

            //riabilitiamo i pulsanti
            run.isEnabled = true
            shuffle.isEnabled = true
            confermaElementi.isEnabled = true
            canvas.update(0)
        }

        fun bubbleSort()
        {
            var temp: ImmagineNumero
            var ultimo = arraySapobully.lastIndex - 1
            for(i in 0..arraySapobully.lastIndex)
            {
                for(j in 0..ultimo)
                {
                    if(arraySapobully[j].numero <= arraySapobully[j + 1].numero)
                        ;
                    else
                    {
                        temp = arraySapobully[j]
                        arraySapobully[j] = arraySapobully[j + 1]
                        arraySapobully[j + 1] = temp
                    }

                    canvas.update(j)
                }
                ultimo--
            }
        }

        fun coctailShakerSort()
        {
            var swapped: Boolean
            var primo = 0
            var ultimo = 0
            do {
                swapped = false
                for(i in primo until arraySapobully.lastIndex - ultimo) {
                    if(arraySapobully[i].numero > arraySapobully[i + 1].numero) {
                        //swap
                        var temp = arraySapobully[i]
                        arraySapobully[i] = arraySapobully[i + 1]
                        arraySapobully[i + 1] = temp
                        swapped = true
                    }
                    canvas.update(i)
                }
                ultimo++

                //al contrario
                for(i in arraySapobully.lastIndex - ultimo downTo primo + 1) {
                    if(arraySapobully[i].numero < arraySapobully[i - 1].numero) {
                        //swap
                        var temp = arraySapobully[i]
                        arraySapobully[i] = arraySapobully[i - 1]
                        arraySapobully[i - 1] = temp
                        swapped = true
                    }
                    canvas.update(i)
                }
                primo++
                //println("Swapped: $swapped Primo: $primo Ultimo: $ultimo")
            }while(swapped)
            canvas.update(0)//lo metto per far sí che la barra sia alla posizione 0
        }

        fun combSort()
        {
            var gap = arraySapobully.size
            var shrink = 1.3
            var sorted = false

            while(!sorted)
            {
                gap = (floor(gap / shrink)).toInt()
                if(gap <= 1) {
                    gap = 1
                    sorted = true
                }

                var i = 0
                while(i + gap < arraySapobully.size)
                {
                    if(arraySapobully[i].numero > arraySapobully[i + gap].numero)
                    {
                        //swap
                        var temp = arraySapobully[i]
                        arraySapobully[i] = arraySapobully[i + gap]
                        arraySapobully[i + gap] = temp
                        sorted = false
                        canvas.update(i, i + gap)
                    }

                    i++
                }
            }
            canvas.update(0)
        }

        fun selectionSort()
        {
            var temp: ImmagineNumero
            var indicePiccolo: Int
            var primo = 1
            for(i in 0..arraySapobully.lastIndex)
            {
                indicePiccolo = primo - 1
                for(j in primo..arraySapobully.lastIndex)//trova il minore
                {
                    if(arraySapobully[j].numero < arraySapobully[indicePiccolo].numero)
                    {
                        indicePiccolo = j
                    }
                    canvas.update(j, indicePiccolo)
                }
                temp = arraySapobully[i]
                arraySapobully[i] = arraySapobully[indicePiccolo]
                arraySapobully[indicePiccolo] = temp

                canvas.update(arraySapobully.lastIndex)

                primo++
            }
        }

        fun doubleSelectionSort()
        {
            fun swap(i: Int, j: Int)
            {
                val temp = arraySapobully[i]
                arraySapobully[i] = arraySapobully[j]
                arraySapobully[j] = temp
            }

            var max: Int
            var min: Int
            var i: Int
            var k: Int
            i = 0
            while (i < arraySapobully.size - i) {
                max = i
                min = i
                //find min and max
                k = i
                while (k < arraySapobully.size - i) {
                    if (arraySapobully[k].numero > arraySapobully[max].numero) {
                        max = k
                    } else {
                        if (arraySapobully[k].numero < arraySapobully[min].numero) {
                            min = k
                        }
                    }
                    canvas.update(k, max, min)
                    k++
                }
                //max swap
                swap(max, arraySapobully.size - i - 1)

                if (arraySapobully.size - 1 - i == min)
                    min = max
                //min swap
                swap(min, i)

                //canvas.update(arraySapobully.lastIndex)

                i++
            }
        }

        fun countingSort()
        {
            var max = 0
            for(i in 0..arraySapobully.lastIndex)//trova il massimo
            {
                if(arraySapobully[i].numero > max)
                    max = arraySapobully[i].numero
                canvas.update(i)
            }

            var arrayOccorrenze = Array<ImmagineNumero?>(max + 1){i -> null}

            for(i in 0..arraySapobully.lastIndex)//conta le occorrenze
            {
                arrayOccorrenze[arraySapobully[i].numero] = arraySapobully[i]
                canvas.update(i)
            }

            var sommaProgressiva = 0
            for(i in 0..arrayOccorrenze.lastIndex)//ordina l'arraay
            {
                //for(j in 0 until arrayOccorrenze[i])
                //{
                    arraySapobully[sommaProgressiva] = arrayOccorrenze[i]!!
                    canvas.update(sommaProgressiva)
                    sommaProgressiva++
                //}
            }
        }

        fun mergeSort(number: Array<ImmagineNumero> = arraySapobully): Array<ImmagineNumero>
        {
            var primaMeta: Array<ImmagineNumero>
            var secondaMeta: Array<ImmagineNumero>

            if (number.size > 1) {
                primaMeta = Array<ImmagineNumero>(Math.ceil(number.size.toDouble() / 2).toInt()){i -> number[i]}//arrotondato all'intero maggiore
                secondaMeta = Array<ImmagineNumero>(number.size - primaMeta.size){i -> number[i + primaMeta.size]}//arrotondato all'intero minore

                primaMeta = mergeSort(primaMeta)
                trueIndex += primaMeta.size//siccome abbiamo un algoritmo divide et impera, per modificare le barre giuste del visualizzatore ho ideato la variabile trueIndex, che ovviamente indica l'indice corretto (nell'array di partenza grande e disordinato) dell'elemento a cui ci stiamo riferendo
                //trueIndex funziona cosí: Viene tranquillamente fatto l'ordinamento della prima metá, viene aumentato trueIndex della dimensione di primaMeta, perché secondaMeta nell'array piú grosso inizierá subito dopo primaMeta, e succesivamente all'ordinamento di secondameta a trueIndex viene sottratto primaMeta.size
                secondaMeta = mergeSort(secondaMeta)
                trueIndex -= primaMeta.size

                //da qui inizia il merge
                var i = 0
                var j = 0
                while (i < primaMeta.size && j < secondaMeta.size) {
                    val currentMin: ImmagineNumero

                    if (primaMeta[i].numero > secondaMeta[j].numero) {
                        currentMin = secondaMeta[j]
                        number[i + j] = currentMin
                        arraySapobully[i + j + trueIndex] = currentMin
                        j++
                    } else {
                        currentMin = primaMeta[i]
                        number[i + j] = currentMin
                        arraySapobully[i + j + trueIndex] = currentMin
                        i++
                    }
                    canvas.update(i + j + trueIndex)
                }

                //da qui inizia l'ultima parte del merge, in cui viene copiato ció che rimane
                while (j < secondaMeta.size) {
                    number[i + j] = secondaMeta[j]
                    arraySapobully[i + j + trueIndex] = secondaMeta[j]
                    canvas.update(i + j + trueIndex)
                    j++
                }
                while (i < primaMeta.size) {
                    number[i + j] = primaMeta[i]
                    arraySapobully[i + j + trueIndex] = primaMeta[i]
                    canvas.update(i + j + trueIndex)
                    i++
                }
            }
            return number
        }

        fun quickSort(number: Array<ImmagineNumero> = arraySapobully): Array<ImmagineNumero>
        {
            if(number.size > 2) {
                var indicePivot = number.lastIndex
                var itemFromLeft = number.lastIndex//partendo da sinistra é il primo numero piú grande del numero number[indicePivot].numero
                var itemFromRight = 0//partendo da destra é il primo numero piú piccolo del numero number[indicePivot].numero
                do{
                    //trova itemFromLeft
                    itemLeft@for(i in 0 until number.lastIndex) {
                        if (number[i].numero > number[indicePivot].numero) {
                            itemFromLeft = i
                            canvas.update(i + trueIndex)
                            break@itemLeft
                        }
                    }
                    //trova itemFromRight
                    itemRight@for(i in number.lastIndex - 1 downTo 0) {
                        if (number[i].numero < number[indicePivot].numero) {
                            itemFromRight = i
                            canvas.update(i + trueIndex)
                            break@itemRight
                        }
                    }

                    if(itemFromLeft < itemFromRight)//se c'é da eseguire lo swap lo esegue
                    {
                        var temp = number[itemFromLeft]
                        number[itemFromLeft] = number[itemFromRight]
                        number[itemFromRight] = temp
                        arraySapobully[trueIndex + itemFromLeft] = number[itemFromLeft]
                        arraySapobully[trueIndex + itemFromRight] = number[itemFromRight]
                        canvas.update(itemFromLeft + trueIndex, itemFromRight + trueIndex)
                    }
                }while(itemFromLeft < itemFromRight)

                //swappo itemFromleft e indicePivot
                var temp = number[itemFromLeft]
                number[itemFromLeft] = number[indicePivot]
                number[indicePivot] = temp
                arraySapobully[trueIndex + itemFromLeft] = number[itemFromLeft]
                arraySapobully[trueIndex + indicePivot] = number[indicePivot]
                canvas.update(itemFromLeft + trueIndex, indicePivot + trueIndex)

                indicePivot = itemFromLeft
                var primaMeta = Array<ImmagineNumero>(indicePivot){i -> number[i]}
                var secondaMeta = Array<ImmagineNumero>(number.size - primaMeta.size - 1){i -> number[i + indicePivot + 1]}

                //System.arraycopy(quickSort(primaMeta), 0, number, 0, primaMeta.size)
                primaMeta = quickSort(primaMeta)
                for(i in 0 until primaMeta.size) {
                    number[i] = primaMeta[i]
                    arraySapobully[i + trueIndex] = number[i]
                    //canvas.update(i + trueIndex)
                }
                trueIndex += primaMeta.size + 1//trueIndex e giusto, devo solo capire come implementare per bene il copiaArray
                //System.arraycopy(quickSort(secondaMeta), 0, number, indicePivot + 1, secondaMeta.size)
                secondaMeta = quickSort(secondaMeta)
                for(i in 0 until secondaMeta.size) {
                    number[i + indicePivot + 1] = secondaMeta[i]
                    arraySapobully[i + trueIndex] = number[i + indicePivot + 1]
                    //canvas.update(i + trueIndex)
                }
                trueIndex -= primaMeta.size + 1
            }
            else if (number.size == 2)
            {
                if(number[0].numero > number[1].numero)
                {
                    var temp = number[0]
                    number[0] = number[1]
                    number[1] = temp
                }
            }
            return number
        }

        fun insertionSort()
        {
            var temp: ImmagineNumero
            var k: Int//contatore che decresce per trovare il valore in cui inserire l'elemento i
            var continua=true//diventa false quando o siamo arrivati a k = 0 o siamo arrivati nella posizione giusta dove va effettuato lo scambio, oppure se il numero in poisizione i é giá maggiore di quello precedente e non va cambiato
            for(i in 1..arraySapobully.lastIndex) {
                temp = arraySapobully[i]
                k = i - 1
                while(continua) {
                    if (temp.numero < arraySapobully[k].numero) {
                        if (k == 0) {
                            arraySapobully[k + 1] = arraySapobully[k]
                            arraySapobully[k] = temp
                            canvas.update(k, i)
                            continua = false;
                        }

                        else {
                            arraySapobully[k+1] = arraySapobully[k]
                            canvas.update(k, i)

                            if (temp.numero >= arraySapobully[k - 1].numero) {
                                arraySapobully[k] = temp
                                canvas.update(k, i)
                                continua = false;
                            }
                        }
                    }

                    else
                        continua = false
                    k--
                }
                continua = true
                canvas.update(i)
            }
        }
    }

    inner class Canvas: JLabel()
    {
        var barraAccesso: BufferedImage = creaBarraAccesso()
        var posizioneBarra = arrayOf(0)
        public override fun paintComponent(g: Graphics)
        {
            var printoBarra: Boolean
            for(i in 0..arraySapobully.lastIndex) {
                printoBarra = false
                controllobarra@for(j in 0..posizioneBarra.lastIndex) {
                    if(posizioneBarra[j] == i) {
                        printoBarra = true
                        break@controllobarra
                    }
                }
                if(printoBarra) {
                    g.drawImage(arraySapobully[i].immagine, i * spessore, 0, null)
                    g.drawImage(barraAccesso, i * spessore, 0, null)
                }
                else
                    g.drawImage(arraySapobully[i].immagine, i * spessore, 0, null)
            }
        }

        fun update(vararg index: Int)
        {
            /*if(index.isEmpty()){
                posizioneBarra = arrayOf(posizioneBarra[0])
            }
            else*/
            posizioneBarra = index.toTypedArray()
            var currentMillis = System.currentTimeMillis()
            while (System.currentTimeMillis() < currentMillis + tempoDiUpdate) {}
            //currentMillis = System.currentTimeMillis()
            paintComponent(this.graphics)
            //println(System.currentTimeMillis() - currentMillis)
        }
    }
}