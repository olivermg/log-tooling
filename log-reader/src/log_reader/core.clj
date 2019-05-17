(ns log-reader.core
  (:gen-class)
  (:require [log-reader.reader :as r]
            [log-reader.nodes-map :as nm]
            [log-reader.output :as o]
            [log-reader.webserver :as w]))

(defn -main [& args]
  (->> (r/read-stream *in*)
       (nm/store-loglines {})
       (o/format)
       (w/serve)))



#_(let [#_in-str #_(str "{:trace [{:id :a :fn :afn :args :aargs :time 2} {:id :aa :fn :aafn :args :aaargs :time 3}] :x :aa}"
                  "{:trace [{:id :b :fn :bfn :args :bargs :time 1} {:id :ba :fn :bafn :args :baargs :time 2}] :x :ba}"
                  "{:trace [{:id :b :fn :bfn :args :bargs :time 3} {:id :bb :fn :bbfn :args :bbargs :time 4} {:id bba :fn :bbafn :args :bbaargs :time 5}] :x :bba}"
                  "{:trace [{:id :a :fn :afn :args :aargs :time 4} {:id :ab :fn :abfn :args :abargs :time 5}] :x :ab}"
                  "{:trace [{:id :a :fn :afn :args :aargs :time 4} {:id :ab :fn :abfn :args :abargs :time 5} {:id :aba :fn :abafn :args :abaargs :time 6}] :x :aba}"
                  "{:trace [{:id :a :fn :afn :args :aargs :time 4} {:id :ab :fn :abfn :args :abargs :time 5} {:id :abb :fn :abbfn :args :abbargs :time 7}] :x :abb}")
      in-str (slurp "testlog4.log")]
  (with-in-str in-str
    (let [srv (-main)]
      (Thread/sleep 10000)
      (srv))))
