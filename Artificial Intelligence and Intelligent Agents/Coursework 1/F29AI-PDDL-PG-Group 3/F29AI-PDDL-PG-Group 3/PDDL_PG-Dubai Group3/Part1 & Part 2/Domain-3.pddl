(define (domain windfarm-3)
    (:requirements :typing :negative-preconditions)

    (:types
        uuv location data - object
        ship waypoint - location
        image sonar sample - data
    )

    (:predicates
        (at ?u - uuv ?l - location) ; UUV is at a location
        (on-ship ?u-uuv ?s-ship) ; UUV is on a ship
        (connected ?from-location ?to-location) ; Locations are connected
        (can-deploy ?u - uuv) ; UUV can be deployed
        (uuv-deployed ?u - uuv) ; UUV is deployed
        (memory-empty ?u - uuv); UUV memory is empty
        (has-data ?u-uuv ?d-data);  UUV has data 
        (data-saved ?d - data ?l-location); Data is saved at a location
        (sample-at ?s - sample ?w-waypoint);    Sample is at a waypoint
        (image-at ?img - image ?w-waypoint);    Image is at a waypoint
        (sonar-at ?snr - sonar ?w - waypoint);  Sonar is at a waypoint
        (sample-stored ?s - sample ?ship - ship);   Sample is stored on a ship

        (can-store-sample ?ship-ship)
        ;; Predicates to link UUVs with their respective ships
        (assigned-to ?u - uuv ?s - ship)          ; UUV is assigned to a specific ship
    )


    (:action deploy-uuv
        :parameters (?u - uuv ?s - ship ?l - location)
        :precondition (and
            (on-ship ?u ?s)
            (connected ?s ?l)
            (can-deploy ?u)
            (assigned-to ?u ?s)       ;; Ensure UUV is assigned to the ship
        )
        :effect (and
            (not (on-ship ?u ?s))
            (at ?u ?l)
            (not (can-deploy ?u))
            (uuv-deployed ?u)
        )
    )

    (:action move-uuv
        :parameters (?u - uuv ?from -location ?to -location)
        :precondition (and
            (at ?u ?from)
            (connected ?from ?to)
            (uuv-deployed ?u)
        )
        :effect (and
            (not (at ?u ?from))
            (at ?u ?to)
        )
    )

    (:action capture-image
        :parameters (?u - uuv ?w - waypoint ?img - image)
        :precondition (and
            (at ?u ?w)
            (image-at ?img ?w)
            (memory-empty ?u)
        )
        :effect (and
            (has-data ?u ?img)
            (not (memory-empty ?u))
        )
    )
    
    (:action perform-sonar-scan
        :parameters (?u - uuv ?w - waypoint ?snr - sonar)
        :precondition (and
            (at ?u ?w)
            (sonar-at ?snr ?w)
            (memory-empty ?u)
        )
        :effect (and
            (has-data ?u ?snr)
            (not (memory-empty ?u))
        )
    )
    
    (:action transmit-data
        :parameters (?u - uuv ?d - data ?l - location ?s - ship)
        :precondition (and
            (at ?u ?l)
            (has-data ?u ?d)
            (assigned-to ?u ?s)          ;; Ensure UUV is assigned to the ship
        )
        :effect (and
            (not (has-data ?u ?d))
            (data-saved ?d ?l)
            (memory-empty ?u)
        )
    )
    
    (:action collect-sample
        :parameters (?u - uuv ?w - waypoint ?s - sample)
        :precondition (and
            (at ?u ?w)
            (sample-at ?s ?w)
            (memory-empty ?u)
        )
        :effect (and
            (has-data ?u ?s)
            (not (sample-at ?s ?w))
            (not (memory-empty ?u))
        )
    )
    
    (:action store-sample
        :parameters (?u - uuv ?s - sample ?ship - ship)
        :precondition (and
            (at ?u ?ship)
            (has-data ?u ?s)
            (assigned-to ?u ?ship)          ;; Ensure UUV is assigned to the ship
            (can-store-sample ?ship)
        )
        :effect (and
            (not (has-data ?u ?s))
            (sample-stored ?s ?ship)
            (not (can-store-sample ?ship))
            (memory-empty ?u)
        )
    )
    
    (:action return-to-ship
        :parameters (?u - uuv ?w - waypoint ?s - ship)
        :precondition (and
            (at ?u ?w)
            (connected ?w ?s)
            (uuv-deployed ?u)
            (assigned-to ?u ?s)       ;; Ensure UUV is assigned to the ship
        )
        :effect (and
            (not (at ?u ?w))
            (on-ship ?u ?s)
            (can-deploy ?u)
            (not (uuv-deployed ?u))
        )
    )
)