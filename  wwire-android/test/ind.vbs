' **** DIM

' vertex
Dim va(144)
Dim va_pos
va_pos = 0

' normals
Dim np1(71)
Dim np2(71)

' **** Initial data          
'lp * 2 * ((lt2 - 1) * 2 + 1) + 1

lp = 6
lp2 = lp / 2   '3
lt = 12
lt2 = lt / 2   '6
lt4 = lt2 / 2  '3

' **** MAIN PROCEDURE START *****

WScript.Echo lp * 2 * ((lt2 - 1) * 2 + 1) + 1

for n = 0 to lp2 - 1
  F1F n
  if n<(lp2 - 1) then
    F1B n
  else
    F2B n
  end if
next

for n = 0 to lp2 - 1
  F3F n
  if n<(lp2 - 1) then
    F3B n
  else
    F4B 
  end if
next

PrintA(va)
PrintNP

' **** MAIN PROCEDURE END   *****

Sub AppendVA(v)
  va(va_pos) = v
  ' WScript.echo "va_pos = " & va_pos
  va_pos = va_pos + 1
End Sub

Sub F1F(n)
  offs = n * 2 * lt

  id0 = offs + 1
  id1 = offs
  id2 = offs + 1 + lt


  'normals
  np1(id1) = id0
  np2(id1) = id2

  'vertex
  AppendVA id1 

  for i = 1 to  lt2 - 1
    id0 = offs + i - 1
    id1 = offs + i
    id2 = offs + i + lt

    'normals
    np1(id1) = id2
    np2(id1) = id0
    np1(id2) = id1
    np2(id2) = id1 + 1

    'vertex
    AppendVA id1
    AppendVA id2
  next

  AppendVA id2 + 1

End Sub

Sub F1B(n)
  offs = n * 2 * lt  + lt 

  id0 = offs - 1 + lt + lt2
  id1 = offs + lt2
  id2 = offs + lt2 - 1

  'normals
  np1(id1) = id0
  np2(id1) = id2

  'vertex
  AppendVA id1 

  for i = - 1 + lt2 to 1 step -1
    id0 = offs + i + 1
    id1 = offs + i + lt
    id2 = offs + i

    'normals
    np1(id1) = id0
    np2(id1) = id2

    'vertex       	
    AppendVA id1
    AppendVA id2

  next

  AppendVA id1 - 1

End Sub

Sub F2B(n)
  offs = n * 2 * lt  + lt 

  id0 = offs + lt2 - 1
  id1 = offs + lt2
  id2 = lt2 + 1

  'normals

  np1(id1) = id0
  np2(id1) = id2

  'vertex
  AppendVA id1 

  for i = - 1 + lt2 to 1 step -1
    id0 = offs + i + 1
    id1 = lt - i 
    id2 = offs + i

    'normals
    np1(id1) = id0
    np2(id2) = id2

    'vertex
    AppendVA id1
    AppendVA id2

  next

  AppendVA id1 + 1

End Sub

Sub F3F(n)
  offs = n * 2 * lt + lt2

  id0 = offs + lt2 - 1
  id1 = offs + lt2
  id2 = offs + lt2 + lt - 1


  'normals
   np1(id1) = id0
   np2(id1) = id2

  'vertex
  AppendVA id1

  for i = lt2 - 1 to 1 step -1
     id0 = offs + i + 1
     id1 = offs + i
     id2 = offs + i + lt

     'normals
     np1(id1) = id2
     np2(id1) = id0
     np1(id2) = id1
     np2(id2) = id1 - 1

     'vertex
     AppendVA id1
     AppendVA id2

  next

  AppendVA id1 - 1

End Sub

Sub F3B(n)
  offs = n * 2 * lt + lt + lt2

  id0 = offs + 1 + lt
  id1 = offs - lt
  id2 = offs + 1

  'normals
  np1(id1) = id0
  np2(id1) = id2

  'vertex
  AppendVA id1

  for i = 1 to lt2 - 1
    id0 = offs + i - 1 - lt
    id1 = offs + lt + i
    id2 = offs + i

    'normals
     np1(id1) = id0
     np2(id1) = id2

     'vertex
     AppendVA id1
     AppendVA id2

  next

  AppendVA id1 + 1

End Sub

Sub F4B 
  id0 = lt * lt2 - lt2 + 1
  id1 = lt * lt2 - lt - lt2
  id2 = lt2 - 1

  'normals
  np1(id1) = id0
  np2(id1) = id2

  'vertex
  AppendVA id1

  for i = lt2 - 1 to 1 step -1

    id1 = i
    id2 = lt * lt2 - i

    'vertex
    AppendVA id1
    AppendVA id2

  next

  AppendVA 0
  AppendVA 0

End Sub


Sub PrintNP
  for i = LBound(np1) to UBound(np1)
    s = "i=" & i & " (" & np1(i) & "-" & i & "," & np2(i) & "-" & i & ")"
    WScript.echo s
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
