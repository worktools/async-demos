
(ns app.demos
  (:require [cljs.core.async :refer [go <! chan onto-chan! to-chan! alts! timeout mult]]
            [cljs.core.async :as async]))

(defn secs [x] (* 1000 x))

(defn fake-task-chan [message bound ret]
  (go
   (let [t (rand-int bound)] (println message "will take" t "secs") (<! (timeout (secs t))))
   (println message "finished")
   ret))

(defn demo-all []
  (go
   (let [tasks (->> (range 10) (map (fn [x] (fake-task-chan (str "rand task " x) 10 x))))]
     (println
      "result"
      (loop [acc [], xs tasks]
        (if (empty? xs) acc (recur (conj acc (<! (first xs))) (rest xs))))))))

(defn demo-alt-syntax []
  (let [<search1 (fake-task-chan "search1" 10 "search1 found x1")
        <search2 (fake-task-chan "search2" 10 "search2 found x2")
        <log (chan)
        <wait (fake-task-chan "timeout" 10 nil)]
    (go
     (loop []
       (let [t (rand-int 10)]
         (println "read log waits" t)
         (<! (timeout (* 1000 t)))
         (println "got log" (<! <log))
         (recur))))
    (go
     (println
      "result"
      (async/alt!
       <wait
       :timeout
       [[<log :message]]
       :sent-log
       [<search1 <search2]
       ([v ch] (do (println "got" v "from" ch) :hit-search)))))))

(defn demo-alts []
  (go
   (let [<search (fake-task-chan "searching" 20 "searched x")
         <cache (fake-task-chan "looking cache" 15 "cached y")
         <wait (fake-task-chan "timeout" 15 nil)
         [v ch] (alts! [<cache <search <wait])]
     (if (= ch <wait ) (println "final: timeout") (println "get result:" v)))))

(defn display-all
  ([<ch] (display-all <ch nil))
  ([<ch message]
   (go
    (loop []
      (let [x (<! <ch)]
        (if (some? message) (println message x) (println x))
        (when (some? x) (recur)))))))

(defn demo-map []
  (let [<c1 (to-chan! (range 10))
        <c2 (to-chan! (range 100 120))
        <c3 (async/map + [<c1 <c2])]
    (display-all <c3)))

(defn demo-merge []
  (let [<c1 (chan), <c2 (chan), <c3 (async/merge [<c1 <c2])]
    (go (>! <c1 "a") (>! <c2 "b"))
    (display-all <c3)))

(defn demo-mix []
  (let [<c0 (chan)
        <c1 (async/to-chan! (range 40))
        <c2 (async/to-chan! (range 100 140))
        mix-out (async/mix <c0)]
    (async/admix mix-out <c1)
    (async/admix mix-out <c2)
    (go
     (doseq [x (range 20)] (println "loop1" (<! <c0)))
     (println "removing c2")
     (async/unmix mix-out <c2)
     (doseq [x (range 20)] (println "loop2" (<! <c0))))))

(defn demo-mult []
  (let [<c0 (async/to-chan! (range 10)), <c1 (chan), <c2 (chan), mult-c3 (async/mult <c0)]
    (async/tap mult-c3 <c1)
    (async/tap mult-c3 <c2)
    (display-all <c1 "from c1")
    (comment "need to take from c2, or c0 is blocked")
    (display-all <c2 "from c2")))

(defn demo-pipeline-filter []
  (let [<c1 (to-chan! (range 20)), <c2 (chan)]
    (async/pipeline 1 <c2 (filter even?) <c1)
    (display-all <c2)))

(defn demo-split []
  (let [<c0 (to-chan! (range 20))]
    (let [[<c1 <c2] (async/split odd? <c0)]
      (go (display-all <c2 "from c2"))
      (go (display-all <c1 "from c1")))))

(defn demo-transduce-filter []
  (let [<c1 (to-chan! (range 20)), <c2 (chan 1 (comp (filter even?)))]
    (async/pipe <c1 <c2)
    (display-all <c2)))
