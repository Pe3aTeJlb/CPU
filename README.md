# CPU

This programm implement Simple CPU with Harward architecture, custom assembler language and a.k.a IDE.

Command format:

8 bits of command + 8 bits of adress/direct operand

Assambler command list:
- SATR R0 - save accum in register
- SATM 00h - save accum in memory at adress
- SATI @R0 - save accum in memory inderect
                
- LDAR R0 - load to accum from register
- LDAM 00h - load to accum from memory
- LDAD #00h - load to accum direct byte
- LDAI @R0 - load to accum from memory inderect
- XCH AB - exchange A B               
                
- ADDR R0 - Add register to accum
- ADDI @R0 - Add inderect byte
- ADDC R0 - Add with carry
- INC R0 - increment register
- DEC R0 - decrement register
- MUL AB - multiply AB - result (B)(A) <= (A)*(B) 
                
- JNZ loop - Jump if not null

GUI Locale - Russian

# Showcase
<img src="https://github.com/Pe3aTeJlb/Pe3aTeJlb/blob/main/ReadmeResources/CPU/main.PNG" width="900" height="600" />

# Technology stack
- Java SE 11
- JavaFX 11.0.2

# Licence
This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

# Contacts
Email: shepuhin@yandex.ru  
Telegram: @Pe3aTeJlb  
Or visit my [website](https://sites.google.com/view/pplosstudio/%D0%B3%D0%BB%D0%B0%D0%B2%D0%BD%D0%B0%D1%8F)
