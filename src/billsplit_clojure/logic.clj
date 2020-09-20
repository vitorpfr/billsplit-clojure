(ns billsplit-clojure.logic
  (:require [schema.core :as s]
            [billsplit-clojure.model :as m]))

(s/set-fn-validation! false)

; section 1: add data to bill

(s/defn bill :- m/Bill []
  {})

(s/defn ^:private person :- m/Person
  [name :- s/Str]
  {:name     name
   :products []})

(s/defn ^:private add-person :- m/Bill
  [bill :- m/Bill
   person-name :- s/Str]
  (assoc bill person-name (person person-name)))

(s/defn add-people-list-to-bill :- m/Bill
  [bill :- m/Bill
   people-list :- [s/Str]]
  (reduce add-person bill people-list))

(s/defn product
  [name quantity price id]
  {:name     name
   :quantity quantity
   :price    price
   :id       id})

(s/defn add-product-to-person :- m/Bill
  [bill :- m/Bill
   person-name :- s/Str
   product :- m/Product]
  (update-in bill [person-name :products] conj product))

(s/defn add-product-to-bill :- m/Bill
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
               product-fractions)))

(s/defn add-product-list-to-bill
  [bill :- m/Bill
   products :- [m/Product]
   who-consumed]
  (reduce (fn [bill {:keys [id] :as product}]
            (add-product-to-bill bill (get who-consumed id) product))
          bill
          products))

; testing - add-products-to-bill
; remove after tests are written
(let [bill (-> (bill)
               (add-people-list-to-bill ["Vitor" "Manuela"]))
      ; products are already on internal model here
      products [(product "Onion" 1 50.00M 0) (product "Fries" 1 30.00M 1)]
      who-consumed {0 ["Vitor" "Manuela"], 1 ["Vitor"]}]
  (add-product-list-to-bill bill products who-consumed))


; section 2: calculate costs (after bill is ready)
(s/defn calculate-product-cost
  [{:keys [quantity price fraction]} :- m/Product]
  (* quantity price fraction))

(s/defn calculate-person-cost :- m/Person
  [person :- m/Person]
  (let [cost-per-product (map calculate-product-cost (:products person))]
    (assoc person :to-pay (bigdec (reduce + cost-per-product)))))

(defn update-map [m f]
  (reduce-kv (fn [m k v]
               (assoc m k (f v))) {} m))

(s/defn calculate-people-costs :- m/Bill
  [bill :- m/Bill]
  (update-map bill calculate-person-cost))

(s/defn add-tip :- m/Bill
  [bill :- m/Bill
   tip-value]
  (if tip-value
    (update-map bill (fn [person] (update person
                                          :to-pay
                                          #(* (inc tip-value) %))))
    bill))

; testing calculate-costs
; remove after tests are written
(let [bill (-> (bill)
               (add-people-list-to-bill ["Vitor" "Manuela"]))
      products [(product "Onion" 1 50.00 0) (product "Fries" 1 30.00 1)]
      who-consumed {0 ["Vitor" "Manuela"], 1 ["Vitor"]}
      final-bill (add-product-list-to-bill bill products who-consumed)]
  (calculate-people-costs final-bill))
