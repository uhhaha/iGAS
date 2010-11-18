(ns iGas.imageText)

(import '(javax.swing JFrame JPanel))
(import '(java.io File))
(import '(javax.imageio ImageIO))

(import '(java.awt.image BufferedImage))

(def frame (JFrame. "This is image Test Sample"))

(def image-from-file (ImageIO/read (File. "data/pic.jpg")))

(defn image-pannel []
  (proxy [JPanel] []
    (paint [g]
	   (.drawImage g image-from-file 0 0 nil))))
