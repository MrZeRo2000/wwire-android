' **** DIM

' normals
Dim np1(71)
Dim np2(71)

' upper and down points
Dim nu(11)
Dim nnu(5)
Dim nd(11)
Dim nnd(5)

' **** Initial data          
lp = 6
lp2 = lp / 2   '3
lt = 12
lt2 = lt / 2   '6
lt4 = lt2 / 2  '3

' **** MAIN PROCEDURE START *****

'WScript.Echo 1 mod lp * lt

N1
N2
PrintNP

N3
PrintA nnu
PrintA nnd
PrintA nu
PrintA nd


' **** MAIN PROCEDURE END   *****


Sub N1

for k = 0 to lp - 1
  offs = k * lt
  for i = 1 to lt2 - 1
    idx = offs + i
    np1(idx) = idx + 1
    if k = lp - 1 then
      np2(idx) = lt - i
    else
      np2(idx) = idx + lt
    end if
  next
next

End Sub

Sub N2

for k = 0 to lp - 1
  offs = k * lt + lt2
  for i = 1 to lt2 - 1
    idx = offs + i
    np1(idx) = idx - 1
    if k = lp - 1 then
      np2(idx) = lt2 - i
    else
      np2(idx) = idx + lt
    end if
  next
next

End Sub

Sub N3

for i = 0 to lp - 1
  nnu(i) = i * lt
  nnd(i) = i * lt + lt2
next

for i = 0 to 2 * lp - 1
  if i < lp then
    nu(i) = lt * i + 1
    nd(i) = lt * i + lt2 - 1
  else
    nu(i) = lt * (i - lp) + lt - 1
    nd(i) = lt * (i - lp) + lt2 + 1
  end if
next

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
