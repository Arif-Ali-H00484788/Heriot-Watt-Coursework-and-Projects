(define (problem windfarm-mission-2)
(:domain windfarm-2)

    (:objects
        uuv1 - uuv
        ship1 - ship
        waypoint1 waypoint2 waypoint3 waypoint4 waypoint5 - waypoint
        img-wp5 - image
        sonar-wp3 - sonar
        sample-wp1 - sample
    )

    (:init
        (on-ship uuv1 ship1) ; UUV starts on ship1
        (memory-empty uuv1) ; Initial UUV state
        (can-deploy uuv1) ; Initial UUV state
        (can-store-sample ship1) ; Ship1 can store samples
        
        (connected ship1 waypoint1) ; Ship-waypoint connections
        (connected waypoint1 ship1) ; Ship-waypoint connections

        ; Image and sonar data locations
        (image-at img-wp5 waypoint5)
        (sonar-at sonar-wp3 waypoint3)
        (sample-at sample-wp1 waypoint1)

        (connected waypoint1 waypoint2)
        (connected waypoint2 waypoint3)
        (connected waypoint1 waypoint4)
        (connected waypoint4 waypoint3)
        (connected waypoint3 waypoint5)
        (connected waypoint5 waypoint1)
    )

    (:goal (and
        (data-saved img-wp5 waypoint5)
        (data-saved sonar-wp3 waypoint3)
        (sample-stored sample-wp1 ship1)
        (on-ship uuv1 ship1)
        )
    )
)