(ns billsplit-clojure.logic
  (:require [schema.core :as s]
            [billsplit-clojure.model :as m]))

(s/set-fn-validation! true)

(s/defn bill :- m/Bill
  []
  {})

(s/defn person :- m/Person
  [name :- s/Str]
  {:name     name
   :products []})

(s/defn add-person :- m/Bill
  [bill :- m/Bill
   person-name :- s/Str]
  (assoc bill person-name (person person-name)))

(s/defn add-people-list :- m/Bill
  [bill :- m/Bill
   people-list :- [s/Str]]
  (reduce add-person bill people-list))

(s/defn product :- m/Product
  [name quantity price]
  {:name     name
   :quantity quantity
   :price    (bigdec price)
   :fraction nil})

(s/defn add-product-to-person :- m/Bill
  [bill :- m/Bill
   person-name :- s/Str
   product :- m/Product]
  (update-in bill [person-name :products] conj product))

(s/defn add-product-to-bill :- m/Bill
  "Given a list of people who consumed a product, adds a fraction of the product to each person bill"
  [bill :- m/Bill
   person-list :- [s/Str]
   {:keys [quantity] :as product} :- m/Product]
  (let [fractions (repeat (count person-list) (/ quantity (count person-list)))
        product-fractions (zipmap person-list fractions)]
    (reduce-kv (fn [bill person-name fraction]
                 (add-product-to-person bill
                                        person-name
                                        (assoc product :fraction fraction)))
               bill
               product-fractions))
  )

; testing - add-product-to-bill - remove later
(let [bill (-> (bill)
               (add-person "Vitor")
               (add-person "Manuela"))
      onion (product "Onion rings" 1 49.90)]
  (-> bill
      (add-product-to-bill ["Vitor" "Manuela"] onion))
  )

(s/defn calculate-product-cost
  [{:keys [quantity price fraction]} :- m/Product]
  (* quantity price fraction))
;
;(calculate-product-cost (product "Onion rings" 1 49.90))

(s/defn calculate-person-cost :- m/Person
  [person :- m/Person]
  (let [cost-per-product (map calculate-product-cost (:products person))]
    (assoc person :to-pay (bigdec (reduce + cost-per-product)))))

(defn update-map [m f]
  (reduce-kv (fn [m k v]
               (assoc m k (f v))) {} m))

(s/defn calculate-costs :- m/Bill
  [bill :- m/Bill]
  (update-map bill calculate-person-cost))

 ;testing calculate-costs -  remove later
(let [bill (-> (bill)
               (add-person "Vitor")
               (add-person "Manuela"))
      onion (product "Onion rings" 1 49.90)
      fries (product "Fries" 1 39.90)]
  (-> bill
      (add-product-to-bill ["Vitor" "Manuela"] onion)
      (add-product-to-bill ["Vitor"] fries)
      calculate-costs))
