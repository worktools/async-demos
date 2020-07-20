
(ns app.comp.container
  (:require [hsl.core :refer [hsl]]
            [respo-ui.core :as ui]
            [respo.core :refer [defcomp defeffect <> >> div button textarea span input]]
            [respo.comp.space :refer [=<]]
            [reel.comp.reel :refer [comp-reel]]
            [respo-md.comp.md :refer [comp-md]]
            [app.config :refer [dev?]]
            [respo-ui.comp :refer [comp-button]]
            [app.demos :as demos]))

(defcomp
 comp-container
 (reel)
 (let [store (:store reel)
       states (:states store)
       cursor (or (:cursor states) [])
       state (or (:data states) {:content ""})]
   (div
    {:style (merge ui/global ui/row {:padding 16})}
    (comp-button
     {:text "Try",
      :on-click (fn [e d!]
        (js/console.clear)
        (comment demos/demo-all)
        (comment demos/demo-alts)
        (comment demos/demo-split)
        (comment demos/demo-mult)
        (comment demos/demo-merge)
        (comment demos/demo-mix)
        (comment demos/demo-transduce-filter)
        (comment demos/demo-map)
        (comment demos/demo-alt-syntax)
        (comment demos/demo-pipeline-filter))})
    (when dev? (comp-reel (>> states :reel) reel {})))))
