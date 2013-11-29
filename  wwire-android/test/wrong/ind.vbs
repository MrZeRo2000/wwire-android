' **** DIM

Dim a(2)
Dim ga()
Dim ga_pos
ga_pos = 0

' normals
Dim np1(71)
Dim np2(71)

' **** Initial data          
lp = 6
lt = 12
lt2 = lt / 2
lt4 = lt2 / 2

' **** Max for triangles
max_pos = (lt - 2) * lt * 3

' **** MAIN PROCEDURE START *****

WScript.echo "LT=" & lt
WScript.echo "LT2=" &lt2

CalcA

'WScript.Echo "*** GA *** "
'PrintA ga
'WScript.Echo "*** GA Bounds *** "
'WScript.Echo "(" & LBound(ga) & ", " & UBound(ga) & ")"

CalcNPFromGA

'PrintA np1
'PrintA np2

PrintNP

' **** MAIN PROCEDURE END *****

Sub CalcA

for n = 0 to lt4 - 2
  WScript.echo "*** F1F " & n & "***"
  F1F n
  WScript.echo "***"
  WScript.echo "*** F1B " & n & "***"
  F1B n
  WScript.echo "***"
next

F1F lt4 - 1

F2B

for n = 0 to lt4 - 2
  WScript.echo "*** F3F " & n & "***"
  F3F n
  WScript.echo "***"
  WScript.echo "*** F3B " & n & "***"
  F3B n
  WScript.echo "***"
next

F3F lt4 - 1

F4B

End Sub

Sub F1F(k)

offs = k * 2 * lt

' top point
a(0) = offs
a(1) = offs + 1
a(2) = offs + lt + 1

AppendA
PrintA a

'moving down
for i = offs + 1 to offs + lt2 - 2
  a(0) = i + lt
  a(1) = i
  a(2) = i + 1

  AppendA
  PrintA a

  a(0) = i + lt
  a(1) = i + 1
  a(2) = i + 1 + lt

  AppendA
  PrintA a
next

'bottom point
a(0) = offs + lt2 - 1 + lt
a(1) = offs + lt2 - 1
a(2) = offs + lt2 + lt

AppendA
PrintA a
  
End Sub

Sub F1B(k)

offs = k * 2 * lt

'first bottom
a(0) = offs + (lt2 - 1) + 2 * lt
a(1) = offs + (lt2 - 1) + lt
a(2) = offs + (lt2 - 1) + lt + 1

AppendA
PrintA a

for i = offs + lt2 - 2 to offs + 1 step -1
  a(0) = i + 2 * lt
  a(1) = i + lt + 1
  a(2) = i + 2 * lt + 1 

  AppendA
  PrintA a

  a(0) = i + 2 * lt
  a(1) = i + lt 
  a(2) = i + lt + 1
  
  AppendA
  PrintA a

next

'last top
a(0) = offs + 2 * lt
a(1) = offs + lt + 1
a(2) = offs + 2 * lt + 1 

AppendA
PrintA a

End Sub

Sub F2B()

offs =  (lt4 - 1) * 2 * lt

'first bottom
a(0) = offs + (lt2 - 1) + lt
a(1) = offs + (lt2 - 1) + lt + 1
a(2) = lt2 + 1

AppendA
PrintA a

for i = offs + lt2 - 2 to offs + 1 step -1
  a(0) = offs - i + lt 
  a(1) = i + lt + 1
  a(2) = offs - i + lt - 1
  

  AppendA
  PrintA a

  a(0) = i + lt 
  a(1) = i + lt + 1
  a(2) = offs - i + lt   

  AppendA
  PrintA a

next

'last top
a(0) = lt
a(1) = offs + lt + 1
a(2) = lt - 1 

AppendA
PrintA a

End Sub

Sub F3F(k)

offs = lt + k * 2 * lt

' top point
a(0) = offs
a(1) = offs - 1
a(2) = offs - 1 + lt 

AppendA
PrintA a

'moving down
for i = offs - 1 to offs - lt2 + 2 step -1
  a(0) = i + lt
  a(1) = i
  a(2) = i - 1

  AppendA
  PrintA a

  a(0) = i + lt
  a(1) = i - 1
  a(2) = i - 1 + lt

  AppendA
  PrintA a
next

'bottom point
a(0) = offs + lt2 + 1
a(1) = offs - lt2 + 1
a(2) = offs - lt2

AppendA
PrintA a
  
End Sub

Sub F3B(k)

offs = lt + k * 2 * lt + lt2

'first bottom
a(0) = offs + 1
a(1) = offs - lt
a(2) = offs + lt + 1

AppendA
PrintA a

for i = offs + 1 to offs + lt2 - 2 
  a(0) = i + lt + 1 
  a(1) = i 
  a(2) = i + lt 
  

  AppendA
  PrintA a
  
  a(0) = i + 1   
  a(1) = i 
  a(2) = i + lt + 1
  

  AppendA
  PrintA a

next

'last top
a(0) = offs + lt2 + lt
a(1) = offs + lt2 - 1
a(2) = offs + lt2 + lt - 1

AppendA
PrintA a

End Sub

Sub F4B

a(0) = lt2 - 1
a(1) = lt * lp - lt2 + 1
a(2) = lt * lp - lt - lt2

AppendA
PrintA a

for i = lt2 - 1 to 1 step -1
  a(0) = i - 1
  a(1) = lt * lp - i 
  a(2) = i 
  

  AppendA
  PrintA a

  if i>1 then
    a(0) = i - 1
    a(1) = lt * lp - i + 1
    a(2) = lt * lp - i     
    
    AppendA
    PrintA a
  end if
next

End Sub


Sub PrintA(va)
  s = "("
  d = ""
  for i1 = lbound(va) to ubound(va)
    s = s & d & va(i1)
    d = ", "
  next
  s = s & ")"
  WScript.echo s
End Sub

Sub PrintNP
  for i = LBound(np1) to UBound(np1)
    s = "i=" & i & " (" & np1(i) & "-" & i & "," & np2(i) & "-" & i & ")"
    WScript.echo s
  next
End Sub


Sub AppendA
Dim va(2)

  ga_pos = ga_pos + 3
  REDIM PRESERVE ga(ga_pos - 1)
  ga(ga_pos - 3) = a(0)
  ga(ga_pos - 2) = a(1)
  ga(ga_pos - 1) = a(2)

  if ga_pos>3 then
    va(0) = ga(ga_pos - 6)
    va(1) = ga(ga_pos - 5)
    va(2) = ga(ga_pos - 4)
    CheckA a, va
  end if 

End Sub

Sub CheckA(a, b)
  mc = 0
  for i = 0 to 2
    for j = 0 to 2
       if a(i) = b(j) then mc = mc + 1
    next
  next

  if mc<>2 then WScript.Echo "Match = " & mc
End Sub

Sub CalcNPFromGA
  for i = 0 to (lt - 2) * lt - 1
     np1(ga(i * 3 + 1)) = ga(i * 3)
     np2(ga(i * 3 + 1)) = ga(i * 3 + 2)
  next
  
  for i = 0 to lt2 - 1
    'top
    np1(i * lt) = -10
    np2(i * lt) = -10
    
    'botton
    np1(i * lt + lt2) = -20
    np2(i * lt + lt2) = -20
  next

End Sub

Sub CalcNP
  for k = 0 to lt4 -1
    offs = k * 2 * lt
    for i = offs to offs + lt2 - 1
       if i = offs then
         np1(i) = -10
         np2(i) = -10
       else
         np1(i) = i - 1
         np2(i) = i + lt
       end if
    next

    for i = offs + lt to offs + lt + lt2 - 1
      if i = offs + lt then
         np1(i) = -10
         np2(i) = -10
      else
         np1(i) = i + lt - 1
         np2(i) = i + lt
      end if
    next

    for i = offs + lt2 to offs + lt - 1
      if i = offs + lt2 then
         np1(i) = -20
         np2(i) = -20
      else
         np1(i) = i + 1
         np2(i) = i + lt
      end if
    next

    for i = offs + lt + lt2 to offs + 2 * lt - 1
      if i = offs + lt + lt2 then
         np1(i) = -20
         np2(i) = -20
      else
         np1(i) = i + 1 + lt
         np2(i) = i + lt
      end if
    next
   next

End Sub
