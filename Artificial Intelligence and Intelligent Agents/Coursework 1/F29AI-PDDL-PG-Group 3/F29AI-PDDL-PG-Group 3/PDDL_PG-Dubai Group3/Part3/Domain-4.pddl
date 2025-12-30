(define (domain windfarm-extended-4)
    (:requirements :typing :negative-preconditions)

    (:types
        uuv engineer - object
        location data - object
        ship waypoint - location
        bay control-center - location
        image sonar sample - data
    )

    (:predicates
        (at ?u - uuv ?l - location)
        (on-ship ?u - uuv ?s - ship)
        (engineer-at ?e - engineer ?loc - location)
        (connected ?from - location ?to - location)
        (can-deploy ?u - uuv)
        (uuv-deployed ?u - uuv)
        (memory-empty ?u - uuv)
        (has-data ?u - uuv ?d - data)
        (data-saved ?d - data ?l - location)
        (sample-at ?s - sample ?w - waypoint)
        (image-at ?img - image ?w - waypoint)
        (sonar-at ?snr - sonar ?w - waypoint)
        (sample-stored ?s - sample ?ship - ship)
        (can-store-sample ?ship - ship)

        ;; Predicates to link UUVs with their respective ships, engineers, and facilities
        (assigned-to ?u - uuv ?s - ship)          ; UUV is assigned to a specific ship
        (engineer-for-ship ?e - engineer ?s - ship) ; Engineer is for a specific ship
        (bay-for-ship ?b - bay ?s - ship)         ; Bay is for a specific ship
        (control-for-ship ?c - control-center ?s - ship) ; Control center for a specific ship
    )

    (:action move-engineer
        :parameters (?e - engineer ?from ?to - location)
        :precondition (and
            (engineer-at ?e ?from)
            (connected ?from ?to)
        )
        :effect (and
            (not (engineer-at ?e ?from))
            (engineer-at ?e ?to)
        )
    )

    (:action deploy-uuv
        :parameters (?u - uuv ?s - ship ?l - location ?e - engineer ?b - bay)
        :precondition (and
            (on-ship ?u ?s)
            (connected ?s ?l)
            (can-deploy ?u)
            (engineer-at ?e ?b)
            (assigned-to ?u ?s)       ;; Ensure UUV is assigned to the ship
            (engineer-for-ship ?e ?s) ;; Ensure the engineer is for this ship
            (bay-for-ship ?b ?s)      ;; Ensure the bay is for this ship
        )
        :effect (and
            (not (on-ship ?u ?s))
            (at ?u ?l)
            (not (can-deploy ?u))
            (uuv-deployed ?u)
        )
    )

    (:action move-uuv
        :parameters (?u - uuv ?from ?to - location)
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
        :parameters (?u - uuv ?d - data ?l - location ?e - engineer ?c - control-center ?s - ship)
        :precondition (and
            (at ?u ?l)
            (has-data ?u ?d)
            (engineer-at ?e ?c)
            (assigned-to ?u ?s)          ;; Ensure UUV is assigned to the ship
            (engineer-for-ship ?e ?s)    ;; Ensure the engineer is for this ship
            (control-for-ship ?c ?s)     ;; Ensure the control center is for this ship
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
        :parameters (?u - uuv ?w - waypoint ?s - ship ?e - engineer ?b - bay)
        :precondition (and
            (at ?u ?w)
            (connected ?w ?s)
            (engineer-at ?e ?b)
            (uuv-deployed ?u)
            (assigned-to ?u ?s)       ;; Ensure UUV is assigned to the ship
            (engineer-for-ship ?e ?s) ;; Ensure the engineer is for this ship
            (bay-for-ship ?b ?s)      ;; Ensure the bay is for this ship
        )
        :effect (and
            (not (at ?u ?w))
            (on-ship ?u ?s)
            (can-deploy ?u)
            (not (uuv-deployed ?u))
        )
    )
)