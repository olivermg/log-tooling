(ns log-reader.core
  (:gen-class)
  (:require [log-reader.reader :as r]
            [log-reader.nodes-map :as nm]
            [log-reader.output :as o]))

(defn -main [& args]
  (->> (r/read-stream *in*)
       (nm/store-loglines {})
       (o/format)
       print))



#_(let [in-str (str "{:trace [{:id :a :fn :afn :args :aargs :time 2}] :x :xxx}"
                  "{:trace [{:id :a :fn :afn :args :aargs :time 2} {:id :aa :fn :aafn :args :aaargs :time 3}] :y :yyy}")]
  (with-in-str in-str
    (-main)))
