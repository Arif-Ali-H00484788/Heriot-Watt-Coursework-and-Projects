    (define (problem windfarm-mission-5)
    (:domain windfarm-extended-5)
    
    (:objects
        ; Vehicles and personnel
        uuv1 uuv2 - uuv
        engineer1 engineer2 - engineer
        
        ; Locations
        ship1 ship2 - ship
        waypoint1 waypoint2 waypoint3 waypoint4 waypoint5 waypoint6 - waypoint
        bay1 bay2 - bay ; bay1 for ship1, bay2 for ship2
        control1 control2 - control-center ; control1 for ship1, control2 for ship2
        
        ; Mission data
        img-wp2 img-wp3 - image
        sonar-wp4 sonar-wp6 - sonar
        sample-wp1 sample-wp5 - sample
    )
    
    (:init
        ; Initial positions
        (at uuv1 waypoint2) ; UUV1 starts deployed at waypoint2
        (uuv-stuck-at uuv1 waypoint2) ; UUV1 is stuck at waypoint2
        (on-ship uuv2 ship2) ; UUV2 starts on ship2
        (engineer-at engineer1 bay1) ;Engineer1 starts at ship1's bay
        (engineer-at engineer2 bay2) ; Engineer2 starts at ship2's bay
        
        ;; UUV and engineer assignments
        (assigned-to uuv1 ship1)
        (assigned-to uuv2 ship2)
        (engineer-for-ship engineer1 ship1)
        (engineer-for-ship engineer2 ship2)
        (bay-for-ship bay1 ship1)
        (bay-for-ship bay2 ship2)
        (control-for-ship control1 ship1)
        (control-for-ship control2 ship2)
        

        ; Initial UUV states
        (uuv-deployed uuv1)
        (can-deploy uuv2)
        (memory-empty uuv1)
        (memory-empty uuv2)
        
        ; Ship states
        (can-store-sample ship1)
        (can-store-sample ship2)
        
        ; Ship1 facility connections
        (connected bay1 control1)
        (connected control1 bay1)

        
        ; Ship2 facility connections
        (connected bay2 control2)
        (connected control2 bay2)

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
        
        ; Algae locations
        (algae-at waypoint2)
        (algae-at waypoint4)
        (algae-at waypoint5)

    )
    
    (:goal (and
        ; Image collection goals
        (data-saved img-wp2 waypoint2)
        (data-saved img-wp3 waypoint3)
        
        ; Sonar scan goals
        (data-saved sonar-wp4 waypoint4)
        (data-saved sonar-wp6 waypoint6)
        
        ; Sample collection goals
        (sample-stored sample-wp1 ship1)
        (sample-stored sample-wp5 ship2)

        ; UUV1 and UUV2 return to their respective ships
        (on-ship uuv1 ship1)
        (on-ship uuv2 ship2)
    ) 
  )
)