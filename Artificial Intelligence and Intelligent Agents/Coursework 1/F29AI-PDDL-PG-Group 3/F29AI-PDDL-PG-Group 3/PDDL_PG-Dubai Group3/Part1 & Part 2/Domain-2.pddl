(define (domain windfarm-2)
    (:requirements :typing :negative-preconditions)

    (:types
        uuv - object
        location data - object
        ship waypoint - location
        image sonar sample - data
    )

    (:predicates
        (at ?u - uuv ?l - location)
        (on-ship ?u - uuv ?s - ship)
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

    )

    (:action deploy-uuv
        :parameters (?u - uuv ?s - ship ?l - location )
        :precondition (and
            (on-ship ?u ?s)
            (connected ?s ?l)
            (can-deploy ?u)
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
        :parameters (?u - uuv ?d - data ?l - location)
        :precondition (and
            (at ?u ?l)
            (has-data ?u ?d)
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
        :parameters (?u - uuv ?w - waypoint ?s - ship )
        :precondition (and
            (at ?u ?w)
            (connected ?w ?s)
        )
        :effect (and
            (not (at ?u ?w))
            (on-ship ?u ?s)
            (can-deploy ?u)
            (not (uuv-deployed ?u))
        )
    )
)