In Control the definition of sensors is during the the layout definition. For more information on how to send a layout see Writing\_A\_Controller.

The sensors available are the gyroscope, accelerometer and linear accelerometer. The gyro actually measures the phones rotation in space not rotation change.

**Note: not all devices will have the sensors**

## How To Request Sensor Data ##

  1. In the layout, send the sensor info

The sensor string is 6 characters long. Each characters is 1 or 0. 1 is for send the data and 0 is for don't send the data
Each character stands for an axies. Here is a list of what they stand for:

1: accelerometer x

2: accelerometer y

3: accelerometer z

4: gyro x

5: gyro y

6: gyro z

7: linear accelerometer x

8: linear accelerometer y

9: linear accelerometer z

> 2. Get the data

The data is sent under to tags: _acc:_ for the accelerometer and _gyro:_ for the gyroscope. The data is sent in this format:

```
tag:x axis (if selected):y axis (if selected):z axis (if selected)
```

For example: my sensor request was _110000000_ so I will get the accelerometer x and y data. If I was put my device almost flat on the floor I would get a message like:

```
acc:0.0000351:0.000154
```

The accelerometer and gyroscope sends the data as a float.