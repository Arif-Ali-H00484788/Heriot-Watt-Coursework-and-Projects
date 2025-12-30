(define (problem windfarm-mission-1)
(:domain windfarm-1)

    (:objects
        uuv1 - uuv
        ship1 - ship
        waypoint1 waypoint2 waypoint3 waypoint4 - waypoint
        img-wp3 - image
        sonar-wp4 - sonar
    )

    (:init
        ; UUV starts on ship1
        (on-ship uuv1 ship1)
        
        ; Image and sonar data locations
        (image-at img-wp3 waypoint3)
        (sonar-at sonar-wp4 waypoint4)

        ; Ship-waypoint connections
        (connected ship1 waypoint1)
        (connected waypoint1 ship1)

        ; Bidirectional connections between waypoints
        (connected waypoint1 waypoint2)
        (connected waypoint2 waypoint1)
        (connected waypoint3 waypoint4)
        (connected waypoint4 waypoint3)

        ; Unidirectional connections
        (connected waypoint2 waypoint3)
        (connected waypoint4 waypoint1)

        ; Initial UUV state
        (memory-empty uuv1)
        (can-deploy uuv1)
    )

    (:goal (and
        (data-saved img-wp3 waypoint3)
        (data-saved sonar-wp4 waypoint4)
        (on-ship uuv1 ship1)
        )

    )
)
