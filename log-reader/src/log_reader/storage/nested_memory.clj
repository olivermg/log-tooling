(ns log-reader.storage.nested-memory
  (:require [log-reader.nested-storage :as st]))

(def ^:private +storage+ (atom {}))

(defrecord Item [callinfo message children])
(defrecord Callinfo [fn args])
(defrecord Children [])
(defrecord Message [message])

(defn store-entry [{:keys [trace] :as entry}]
  (reduce (fn [path {:keys [id] :as trace-step}]
            (let [path (conj path id)]
              (swap! +storage+ update-in path #(assoc % :callinfo (dissoc trace-step :id)))
              (conj path :children)))
          [] trace)
  (let [path (->> (map :id trace)
                  (interleave (repeat :children))
                  rest
                  vec)]
    (swap! +storage+ assoc-in (conj path :message) (dissoc entry :trace)))
  @+storage+)

(defn lookup [trace-ids]
  (let [path (-> (interleave (repeat :children) trace-ids)
                 vec)]
    (some-> (get-in @+storage+ path)
            (update :children #(map (fn [[k v]]
                                      (conj (vec trace-ids) k))
                                    %)))))

(defn all-data []
  @+storage+)



#_(do (store-entry {:trace [{:id :a :fn :afn} {:id :aa :fn :aafn}] :x "dataaa"})
    (store-entry {:trace [{:id :b :fn :bfn}] :x "datab"})
    (store-entry {:trace [{:id :a :fn :afn} {:id :ab :fn :abfn}] :x "dataab"})
    (store-entry {:trace [{:id :a :fn :afn} {:id :ab :fn :abfn} {:id :aba :fn :abafn}] :x "dataaba"})
    (store-entry {:trace [{:id :a :fn :afn} {:id :ab :fn :abfn} {:id :abb :fn :abbfn}] :x "dataabb"})
    (clojure.pprint/pprint (all-data))
    (println "=====")
    #_(lookup [:a :ab])
    (-> (clojure.walk/prewalk (fn [d]
                                #_(if (map-entry? d)
                                  (let [[k v] d]
                                    (case k
                                      :callinfo [:div.fn (:fn v)]
                                      :message  [:div.message v]
                                      :children d
                                      d))
                                  d)
                                (cond
                                  #_(map-entry? d) #_(do (println "MAPENTRY" d) d)
                                  (and (map? d)
                                       (contains? d :callinfo)) (do (println "ITEM" d)
                                                                    (->> [:div.step
                                                                          [:div.fn (some-> d :callinfo :fn)]
                                                                          [:div.message (some-> d :message)]
                                                                          (some-> d :children)]
                                                                         (remove nil?)
                                                                         vec))
                                  (map? d) (do (println "CHILDREN" d)
                                               (into [:div.children] (map val d)))

                                  true d))
                              (all-data))
        clojure.pprint/pprint))
