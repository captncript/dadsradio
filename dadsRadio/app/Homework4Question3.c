#include <stdio.h>

#define NROWS 4
#define NCOLS 5

printTwoDArray(int values[NROWS][NCOLS]);

int main() {
    int i;
    int values[NROWS][NCOLS] = {{1,2,3,4,5},{6,7,8,9,10},{11,12,13,14,15},{16,17,18,19,20}};

printTwoDArray(values);

//[0-NROWS-1][0]
//[NROWS-1][1-(NCOLS-1)]
//[(NROWS-2)-0][NCOLS-1]
//[0][(NCOLS-2)-1]
    //Call print array function pulled from other code
    printf("Printing the perimeter counterclockwise.../n");
    for(i=0;i<NROWS;i++) {
        printf("%d,",values[i][0]);
    }
    for(i=1;i<NCOLS-1;i++) {
        printf("%d,",values[NROWS-1][i]);
    }
    for(i=NROWS-2;i>=0;i--) {
        printf("%d,",values[i][NCOLS-1]);
    }
    for(i=NCOLS-2;i>=1;i--) {
        printf("%d",values[i][0]);
        if(i!=1) {
            printf(",");
        }
    }
    
}

printTwoDArray(int values[NROWS][NCOLS]) {
    int i,j;

    for(i=0;i<NROWS;i++) {
        for(j=0;j<NCOLS;j++) {
            printf("%d ", values[i][j]);
        }
        printf("/n");
    }

}










