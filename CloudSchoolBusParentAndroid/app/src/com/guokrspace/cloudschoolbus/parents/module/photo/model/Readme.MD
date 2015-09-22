1) Create DB Model Article and SingleFile, and 1:M relationship
2) Create Model Extending DB models created above to add required functions and members
3) When the upload finishes one file, check DB table Article to see if there are still Single files.
3.1)If all files related to this article are uploaded, send "Over" message and remove the article from the article DB table.
