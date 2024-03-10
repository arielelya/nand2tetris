 (START)
    @SCREEN
    D=A
    @address
    M=D   // address = screen base address

    @color
    M=0


   (KEYBOARD)
    @KBD
    D=M   // D = input from user

    @BLACK
    D;JGT   // if pressing a key

    @WHITE
    D;JEQ   // if not pressing any key

 (BLACK)
    @color
    M=-1   // RAM[color]=-1
    @COLOR_SCREEN
    0;JMP


 (WHITE)
    @color
    M=0   //RAM[color]=0
    @COLOR_SCREEN
    0;JMP
      
 
 (COLOR_SCREEN)
   @color
   D=M   // D contains the color

   @address
   A=M
   M=D  // fill the screen


   @address
   D=M+1   // calculate the next pixel
   @KBD    // check if a key has been pressed
   D=A-D


   @address
   M=M+1   // points to the next pixel
   A=M

   @COLOR_SCREEN
   D; JGT

   @START
   0;JMP