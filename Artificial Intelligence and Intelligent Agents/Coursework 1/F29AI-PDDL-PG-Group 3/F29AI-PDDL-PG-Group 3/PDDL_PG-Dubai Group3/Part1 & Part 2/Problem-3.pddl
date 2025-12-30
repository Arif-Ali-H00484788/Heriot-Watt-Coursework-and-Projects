(define (problem windfarm-mission-3)
(:domain windfarm-3)
        
        (:objects
            ; Vehicles and personnel
            uuv1 uuv2 - uuv
            
            ; Locations
            ship1 ship2 - ship
            waypoint1 waypoint2 waypoint3 waypoint4 waypoint5 waypoint6 - waypoint

            
            ; Mission data
            img-wp2 img-wp3 - image
            sonar-wp4 sonar-wp6 - sonar
            sample-wp1 sample-wp5 - sample
        )
        
        (:init
            ; Initial positions
            (at uuv1 waypoint2) ; UUV1 starts deployed at waypoint2
            (on-ship uuv2 ship2) ; UUV2 starts on ship2

            
            ;; UUV assignments
            (assigned-to uuv1 ship1)
            (assigned-to uuv2 ship2)

            ; Initial UUV states
            (uuv-deployed uuv1)
            (can-deploy uuv2)
            (memory-empty uuv1)
            (memory-empty uuv2)
            
            ; Ship states
            (can-store-sample ship1)
            (can-store-sample ship2)

            
            ; Ship to waypoint connections
            (connected ship1 waypoint2)
            (connected waypoint2 ship1)
            (connected ship2 waypoint3)
            (connected waypoint3 ship2)
            
            
            
            ; Waypoint connections (bidirectional)
            (connected waypoint1 waypoint2)
            (connected waypoint2 waypoint1)
            (connected waypoint2 waypoint3)
            
            (connected waypoint3 waypoint5)
            (connected waypoint5 waypoint3)
            
            (connected waypoint2 waypoint4)
            (connected waypoint4 waypoint2)
            
            (connected waypoint5 waypoint6)
            (connected waypoint6 waypoint4)
            
            ; Sample locations
            (sample-at sample-wp1 waypoint1)
            (sample-at sample-wp5 waypoint5)
            
            ; Image locations
            (image-at img-wp2 waypoint2)
            (image-at img-wp3 waypoint3)

            ; Sonar locations
            (sonar-at sonar-wp4 waypoint4)
            (sonar-at sonar-wp6 waypoint6)

        )
        
        (:goal (and
            ;; UUV1-specific tasks
            (data-saved img-wp2 waypoint2)
            (data-saved sonar-wp4 waypoint4)
            (sample-stored sample-wp1 ship1)
            (on-ship uuv1 ship1)

            ;; UUV2-specific tasks
            (data-saved img-wp3 waypoint3)
            (data-saved sonar-wp6 waypoint6)
            (sample-stored sample-wp5 ship2)
            (on-ship uuv2 ship2)
        )
    )
)