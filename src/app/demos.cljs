
(ns app.demos
  (:require [cljs.core.async :refer [go <! chan onto-chan to-chan alts! timeout]]
            [cljs.core.async :as async]))

(defn demo-all []
  (go
   (let [data (range 10)
         tasks (doall
                (->> data
                     (map
                      (fn [x]
                        (go
                         (println "starting task of" x)
                         (<! (timeout (* 1000 x)))
                         (println "finished task of" x)
                         x)))))
         result (loop [acc [], xs tasks]
                  (if (empty? xs) acc (recur (conj acc (<! (first xs))) (rest xs))))]
     (let [] (println result)))))

(defn demo-alts []
  (go
   (let [<search (go
                  (println "start searching")
                  (let [t (rand-int 20)]
                    (println "searching tasks" t)
                    (<! (timeout (* 1000 t))))
                  (println "finished searching")
                  "searched x")
         <cache (go
                 (println "start looking cache")
                 (let [t (rand-int 15)]
                   (println "looking takes" t)
                   (<! (timeout (* 1000 t))))
                 (println "finished looking cache")
                 "cached y")
         <wait (let [t (rand-int 15)] (println t "to timeout") (timeout (* 1000 t)))
         [v ch] (alts! [<cache <search <wait])]
     (if (= ch <wait ) (println "final: timeout") (println "get result:" v)))))

(defn demo-filter [] )

(defn demo-merge []
  (let [<c1 (chan), <c2 (chan), <c3 (async/merge [<c1 <c2])]
    (go (>! <c1 "a") (>! <c2 "b"))
    (go (loop [] (println (<! <c3)) (recur)))))

(defn demo-mix [] )

(defn demo-split []
  (let [<c0 (chan)]
    (go (doseq [x (range 20)] (>! <c0 x)))
    (let [[<c1 <c2] (async/split odd? <c0)]
      (go (loop [] (let [x (<! <c2)] (println "from c2:" x) (if (some? x) (recur)))))
      (go (loop [] (let [x (<! <c1)] (println "from c1:" x) (if (some? x) (recur))))))))
