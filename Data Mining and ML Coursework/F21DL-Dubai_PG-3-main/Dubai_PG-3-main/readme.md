####  1. Group : Dubai_PG-3

####  2. Project Title :' Predicting  Cardiovascular  Disease  Risk  and  Idenfying  High-Risk  Patients  Using  Machine  Learning.

####  3. Group Members  :
                 HARIKA MAHALAKSHMI SRIDHAR (H00464781) 
                 SYED ARIF ALI (H00484788) 
                 BHAGYA PERERA (H00481008) 
                 PRIYANSHU DWIVEDI (H00330801) 
                 RAHUL KUMAR (H00474374) 

####   4. Research Objectives :
            -Develop the models which can predict cardiovascular disease before symptoms using ML techniques
              * predict presence of heart disease using UCI data set
              * predict 10 year CHD using framuingham data set.
              * predict cardiomegaly using chest X-ray image data set.

            -Identify the risk factors

####   5. Milesstone:
            Week 2 - Week 4  
               *Project Topic, Direction, and Questions [R1]
            Week 3 - Week 6
                *Data Analysis and Exploration [R2]
            Week 6 - Week 8
               *Clustering [R3]
            Week 7 - Week 8
               *Baseline Training and Evaluation Experiments [R4]
            Week 8 - Week 10
               *Neural Networks [R5]

####    6. The sources of  datasets.
               our reserch consists of 3 data sets
                    *Framingham data set : https://www.kaggle.com/datasets/aasheesh200/framingham-heart-study-dataset
                    *UCI data set : https://www.kaggle.com/datasets/redwankarimsony/heart-disease-data/data
                    *Cardiomegaly image data set :https://www.kaggle.com/datasets/rahimanshu/cardiomegaly-disease-prediction-using-cnn

                    
####     7. How to run and data preparation pipeline
                  There are 3 folders in structure
                  Notebooks conatins the code for each 3 data set seperately. all the informations are included in the notebook. how torun, essencial libraries etc
                  data folder contains data set.

####      8.
           R1,R2- performed for Framingham Data Set,Cardiomagaly data set,UCI Data set
           R3-R4 performed for Framingham Data Set,UCI Data set
           R5 performed for Cardiomegaly data set

            Framingham Data Set
          
                | Model               | Accuracy  | Precision | Recall   | F1-Score |
                |---------------------|-----------|-----------|----------|----------|
                | Decision Tree       | 89.85%    | 83.48%    | 98.87%   | 90.52%   |
                | MLP Classifier      | 89.24%    | 83.31%    | 97.67%   | 89.90%   |
                | K-NN                | 78.54%    | 73.12%    | 88.94%   | 80.26%   |
                | Logistic Regression | 66.49%    | 65.38%    | 67.30%   | 66.33%   |
                | Naïve Bayes         | 63.16%    | 67.06%    | 48.87%   | 56.53%   |
                | Perceptron          | 56.34%    | 57.25%    | 43.29%   | 49.30%   |

           Cardiomagaly data set

                | Optimizer | Learning Rate | Batch Size | Cross Fold | Accuracy  | Loss     | Precision | Recall  | F1-Score |
                |-----------|---------------|------------|------------|-----------|----------|-----------|---------|----------|
                | Adam      | 0.001         | 32         | NO         | 0.742369  | 0.54336  | 0.7027    | 0.8402  | 0.7653   |
                | Adam      | 0.001         | 64         | NO         | 0.75852   | 0.54209  | 0.72857   | 0.82405 | 0.77337  |
                | Adam      | 0.001         | 128        | NO         | 0.75044   | 0.54172  | 0.7066    | 0.85637 | 0.77435  |
                | Adam      | 0.001         | 256        | NO         | 0.745062  | 0.539195 | 0.709035  | 0.83123 | 0.765289 |
                | Adam      | 0.001         | 512        | NO         | 0.7459604 | 0.5540325| 0.72682   | 0.78815 | 0.7562446|
                | Adam      | 0.1           | 32         | NO         | 0.5       | 0.7189   | 0.5       | 1.0     | 0.6667   |
                | Adam      | 0.01          | 32         | NO         | 0.5       | 0.693173 | 0.5       | 1.0     | 0.6667   |
                | Adam      | 0.0001        | 32         | NO         | 0.77019   | 0.54761  | 0.74876   | 0.8132  | 0.7796   |
                | Adam      | 0.0001        | 128        | YES        | 0.7628    | 0.543142 | 0.754456  | 0.78276 | 0.767255 |


              UCI Data set

                | Model                | Accuracy | CV Mean | CV Std |
                |----------------------|----------|---------|--------|
                | KNN                 | 0.819    | 0.824   | 0.023  |
                | Logistic Regression | 0.789    | 0.814   | 0.028  |
                | Naive Bayes         | 0.779    | 0.805   | 0.022  |
                | Decision Tree       | 0.775    | 0.772   | 0.016  |
                | MLP                 | 0.760    | 0.778   | 0.044  |
                | Perceptron          | 0.696    | 0.730   | 0.047  |





 #### 9. Folder Structure
                 data -contains data set
                 notebooks -contains codes for each data set
                 documentation-weekly progress



                  

               

               
              
              
              
